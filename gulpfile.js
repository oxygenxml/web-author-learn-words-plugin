var gulp = require('gulp');
var Synci18n = require('sync-i18n');
var concat = require('gulp-concat');
var iife = require("gulp-iife");
var uglify = require('gulp-uglify');

var webLocation = 'web';
var targetLocation = "target";
gulp.task('i18n', function (done) {
  Synci18n().generateTranslations();
  done();
});
gulp.task('prepare-package', gulp.series('i18n', function() {
  return gulp.src(webLocation + '/*.js')
    .pipe(concat('plugin.js'))
    .pipe(iife({useStrict: false, prependSemicolon: true}))
    .pipe(uglify())
    .pipe(gulp.dest(targetLocation));
}));
gulp.task('default', gulp.series('prepare-package'));