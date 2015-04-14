package utils;

import models.User;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

public class CsvUserLoader {
    private File file;

    public CsvUserLoader(File file){
        this.file = file;
    }

    public List<User> read() throws IOException {
        Reader in = new FileReader(file);
        Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader()
                .parse(in);

        List<User> users = new LinkedList<User>();

        for (CSVRecord record : records) {
            User user = new User();

            if(!record.isSet("username")){
                throw new LoaderException("Username is undefined", record);
            }

            user.username = record.get("username");

            if(record.isSet("password")){
                user.setPassword(record.get("password"));
            }
            if(record.isSet("name")){
                user.name = record.get("name");
            }
            if(record.isSet("organization")){
                user.organization = record.get("organization");
            }
            if(record.isSet("type")){
                try {
                    user.type = User.TYPES.valueOf(record.get("type"));
                }catch(IllegalArgumentException e){
                    throw new LoaderException("Unknown user type " + record.get("type"), e, record);
                }
            }

            users.add(user);
        }

        return users;
    }

    public static class LoaderException extends RuntimeException {
        private CSVRecord record;

        public LoaderException(String message, CSVRecord record) {
            super("["+record.getRecordNumber()+"] " + message);
            this.record = record;
        }

        public LoaderException(String message, Throwable cause, CSVRecord record) {
            super("["+record.getRecordNumber()+"] " + message, cause);
        }
    }
}
