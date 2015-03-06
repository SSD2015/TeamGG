package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.avaje.ebean.annotation.Sql;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.collections.map.MultiKeyMap;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.libs.Json;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@JsonSerialize(using = VoteSerializer.class)
public class Vote extends Model {
    @Id
    public Integer id;

    @ManyToOne
    @JoinColumn(name="category_id", referencedColumnName="id")
    public VoteCategory category;

    @ManyToOne
    @JoinColumn(name="user_id", referencedColumnName="id")
    public User user;

    @ManyToOne
    @JoinColumn(name="project_id", referencedColumnName="id")
    public Project project;

    @Constraints.Required
    public int score = 1;

    public Date date;

    public static Model.Finder<Integer, Vote> find = new Model.Finder<Integer, Vote>(
            Integer.class, Vote.class
    );

    public static MultiKeyMap summarize(){
        String sql = "SELECT project.id, project.name, category_id, SUM(vote.score) AS score," +
                "(SELECT COUNT(*) FROM vote v" +
                " INNER JOIN vote_category ON vote_category.id = v.category_id" +
                " WHERE v.category_id = vote.category_id AND (vote.project_id=v.project_id OR vote_category.type=0))" +
                " AS voters" +
                " FROM project" +
                " JOIN vote ON vote.project_id = project.id" +
                " GROUP BY project.id, vote.project_id, vote.category_id";
        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("project.id", "project.id")
                .columnMapping("project.name", "project.name")
                .columnMapping("category_id", "category.id")
                .create();
        Query<VoteAggregate> query = Ebean.find(VoteAggregate.class);
        query.setRawSql(rawSql);

        List<VoteAggregate> result = query.findList();
        MultiKeyMap out = new MultiKeyMap();

        for(VoteAggregate item : result){
            out.put(item.project.id, item.category.id, item);
        }

        return out;
    }

    @Entity
    @Sql
    @JsonIgnoreProperties({"category"})
    public static class VoteAggregate {
        @ManyToOne
        @JsonIgnoreProperties({"group"})
        public Project project;
        @ManyToOne
        public VoteCategory category;

        public int score;
        public int voters;

        public ObjectNode asJson(){
            return (ObjectNode) Json.toJson(this);
        }
    }
}
