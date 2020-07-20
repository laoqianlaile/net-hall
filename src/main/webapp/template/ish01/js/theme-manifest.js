var themeCssRevManifest = {
  "theme-dialog-normal.css": "theme-dialog-normal-48801f8df6.css"
};

function addThemeFile(flag,theme){
  return themeCssRevManifest['theme-'+flag + '-' + (theme||'normal') +'.css'];
}
