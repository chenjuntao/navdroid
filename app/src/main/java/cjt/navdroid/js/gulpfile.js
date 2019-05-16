var gp = require('gulp');
var concat = require('gulp-concat');
var uglify = require('gulp-uglify');

gp.task('default', done => {
        gp.src(['ngraph.graph.min.js',
                'ngraph.path.min.js',
                'astar.js'])
            // .pipe(babel({presets: ['es2015']}))
            .pipe(concat('./main.js'))
            // .pipe(uglify())
            .pipe(gp.dest('dist'));
        done();
});