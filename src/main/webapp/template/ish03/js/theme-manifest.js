var themeCssRevManifest = {
  "theme-dialog-normal.css": "theme-dialog-normal-a98a5911ba.css"
};

function addThemeFile(flag,theme){
  return themeCssRevManifest['theme-'+flag + '-' + (theme||'normal') +'.css'];
}
