var themeCssRevManifest = {
  "theme-dialog-normal.css": "theme-dialog-normal-1c8670dcfb.css"
};

function addThemeFile(flag,theme){
  return themeCssRevManifest['theme-'+flag + '-' + (theme||'normal') +'.css'];
}
