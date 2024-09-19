const urlParams = new URLSearchParams(window.location.search)
const mode = urlParams.get('mode')

/* Ajoute une balise script dans le fichier html 'plateau' */
function loadMode(src) {
    const script = document.createElement('script')
    script.src = src
    const modeDefiner = document.getElementById('modeDefiner')
    document.head.replaceChild(script, modeDefiner)
}


if (mode === 'local') {
    loadMode('../script/PlateauLocal.js')
} else if (mode === 'online') {
    loadMode('../script/PlateauEnLigne.js')
}
