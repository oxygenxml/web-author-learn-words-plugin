var gulp = require('gulp');
var Synci18n = require('sync-i18n');
var concat = require('gulp-concat');
var iife = require("gulp-iife");
var uglify = require('gulp-uglify');
var uglifyOptions = {
  mangle: {
    properties: {
      regex: /_$/
    }
  }
};

var webLocation = 'web';
var targetLocation = "target";

gulp.task('prepare-package', ['i18n'], function() {
  return gulp.src(webLocation + '/*.js')
    .pipe(concat('plugin.js'))
    .pipe(iife({useStrict: false, prependSemicolon: true}))
    .pipe(uglify(uglifyOptions))
    .pipe(gulp.dest(targetLocation));
});

gulp.task('i18n', function () {
  Synci18n({
    // use this flag only if sure that the plugin will be surrounded by IIFE.
    useLocalMsgs:true
  }).generateTranslations();
});

gulp.task('default', ['prepare-package']);