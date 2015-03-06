(function(){

    $('.chart').each(function(){
        var self = this;
        var datapoints = $('.data', this).map(function(index, item){
            var out = JSON.parse(item.dataset['data']);
            return {
                'label': out.project.name,
                'value': out.score
            };
        }).toArray();
        if(this.dataset['type'] == 'BEST_OF'){
            nv.addGraph(function() {
                var chart = nv.models.pieChart()
                    .x(function(d) { return d.label })
                    .y(function(d) { return d.value })
                    .showLabels(true);
                console.log(datapoints);

                d3.select($('svg', self).get(0))
                    .datum(datapoints)
                    .transition().duration(350)
                    .call(chart);

                return chart;
            });
        }
    });

})();