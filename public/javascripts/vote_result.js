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

                d3.select($('svg', self).get(0))
                    .datum(datapoints)
                    .transition().duration(350)
                    .call(chart);

                nv.utils.windowResize(chart.update);

                return chart;
            });
        }else{
            nv.addGraph(function() {
                var chart = nv.models.discreteBarChart()
                    .x(function(d) { return d.label })
                    .y(function(d) { return d.value })
                    .staggerLabels(true)
                    .showValues(true);

                d3.select($('svg', self).get(0))
                    .datum([
                        {'key': 'Score', 'values': datapoints}
                    ])
                    .transition().duration(350)
                    .call(chart);

                nv.utils.windowResize(chart.update);

                return chart;
            });
        }
    });

})();