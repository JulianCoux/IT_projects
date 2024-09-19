import {dirname, join} from "node:path";
import {fileURLToPath} from "node:url";
import express from "express";

const app = express();
const __dirname = dirname(fileURLToPath(import.meta.url));

/* Permet d'utiliser les fichiers js et cypress citées dans les fichiers html */
app.use('/styles', express.static(join(__dirname, '../styles')));
app.use('/script', express.static(join(__dirname, '../script')));

/* Lorqu'une requête GET se fait sur un fichier html, on envoie le 'lien' de ce dernier pour pouvoir y accéder */
app.get('/', (req, res) => {
    res.sendFile(join(__dirname, '../index.html'));
});
app.get('/index.html', (req, res) => {
    res.sendFile(join(__dirname, '../index.html'));
});
app.get('/pages/salon_en_ligne.html', (req, res)=>{
    res.sendFile(join(__dirname, '../pages/salon_en_ligne.html'));
})
app.get('/pages/partie_locale.html', (req, res)=>{
    res.sendFile(join(__dirname, '../pages/partie_locale.html'));
})
app.get('/pages/rejoindre_partie.html', (req, res)=>{
    res.sendFile(join(__dirname, '../pages/rejoindre_partie.html'));
})
app.get('/pages/plateau.html', (req, res)=>{
    res.sendFile(join(__dirname, '../pages/plateau.html'));
})
app.get('/pages/parametres.html', (req, res)=>{
    res.sendFile(join(__dirname, '../pages/plateau.html'));
})
export { app }