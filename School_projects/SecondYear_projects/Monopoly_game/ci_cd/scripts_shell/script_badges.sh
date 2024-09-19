# Badge HTML
NBERR_HTML=$(grep -e "error " ./ci_cd/linthtml_report.txt | wc -l)
NBWARN_HTML=$(grep -e "warning " ./ci_cd/linthtml_report.txt | wc -l)
color="green"
if [[ $NBERR_HTML > 0 ]]
then
    color="brightred"
    else if [[ $NBWARN_HTML > 0 ]]
    then
      color="orange"
    fi
fi
nom_affiche_html="HTML"
nom_svg_html="badge_html.svg"
anybadge -o -l "$nom_affiche_html" -v "$NBERR_HTML $NBWARN_HTML" -c "$color" -f "$nom_svg_html"


# Badge CSS
NBERR_CSS=$(grep -e "ERR" ./ci_cd/lintcss_report.txt | wc -l)
NBWARN_CSS=$(grep -e "WARN" ./ci_cd/lintcss_report.txt | wc -l)
color="green"
if [[ $NBERR_CSS > 0 ]]
then
    color="brightred"
    # else if [[ $NBWARN_CSS > 0 ]]
    # then
    #   color="orange"
    # fi
fi
nom_affiche_css="CSS"
nom_svg_css="badge_css.svg"
anybadge -o -l "$nom_affiche_css" -v "$NBERR_CSS $NBWARN_CSS" -c "$color" -f "$nom_svg_css"


# Badge ESLINT
NBERR_JS=$(grep -e "error " ./ci_cd/lintes_report.txt | wc -l)
NBWARN_JS=$(grep -e "warning " ./ci_cd/lintes_report.txt | wc -l)
color="green"
if [[ $NBERR_JS > 0 ]]
then
    color="brightred"
    else if [[ $NBWARN_JS > 0 ]]
    then
      color="orange"
    fi
fi
nom_affiche_js="JavaScript"
nom_svg_js="badge_js.svg"
anybadge -o -l "$nom_affiche_js" -v "$NBERR_JS $NBWARN_JS" -c "$color" -f "$nom_svg_js"

# Badge Cypress
NBERR_CYPRESS=$(grep -oE 'Failing:\s+[1-9]+' ./ci_cd/cypress_report.txt | wc -l)
color="green"
if [[ $NBERR_CYPRESS > 0 ]]
then
    color="brightred"
fi
anybadge -o -l "Cypress" -v "$NBERR_CYPRESS" -c "$color" -f "badge_cypress.svg"