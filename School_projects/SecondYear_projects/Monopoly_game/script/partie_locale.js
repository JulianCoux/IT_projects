localStorage.clear()
localStorage.setItem('mode', 'local')

// var nomsJoueurs = [];
const nomsJoueurs = JSON.parse(localStorage.getItem('nomsJoueurs')) || [];

// Fonction pour sauvegarder nomsJoueurs dans le stockage local
function sauvegarderNomsJoueurs() {
    localStorage.setItem('nomsJoueurs', JSON.stringify(nomsJoueurs));
}

// Sauvegarder nomsJoueurs dans le stockage local
sauvegarderNomsJoueurs();
document.addEventListener("DOMContentLoaded", function() {

    let joueurCount = 0;

    function supprimerJoueur() {
        joueurCount --;
        
        // Récupérer le nom du joueur à partir du troisième enfant du parent
        const nomJoueur = this.parentNode.children[2].innerHTML;

        // Supprimer le nom du joueur du tableau
        const index = nomsJoueurs.indexOf(nomJoueur);
        if (index !== -1) {
            nomsJoueurs.splice(index, 1);
        }
        // Récupérer le deuxième fils de l'élément joueur
        const deuxiemeFils = this.parentNode.children[1];


        // Supprimer le parent (nouveauJoueur) de l'élément actuel (le bouton -)
        this.parentNode.remove();
        
        reorganisation(index);

        sauvegarderNomsJoueurs();
    }

    function reorganisation(numeroSupprime) {
        const joueursContainer = document.getElementById('joueursContainer');
        if (numeroSupprime !== joueursContainer.childElementCount){
            for (let i = numeroSupprime; i < nomsJoueurs.length; i++) {
                const joueur = joueursContainer.children[i];

                const elementNumero = joueur.querySelector('.numero');
                elementNumero.textContent = 'Joueur ' + (i + 1);
            }
        }
    }

    function estNomValide(nom) {
        // Vérifier si le nom contient uniquement des lettres et des chiffres
        return /^[a-zA-Z][a-zA-Z0-9]*$/.test(nom);
    }
    
    

    function ajouterJoueur() {
        const inputNom = document.getElementById('username');
        const nomJoueur = inputNom.value;
        const tableauMots = nomJoueur.split(' '); // Divise la chaîne en un tableau de mots
        const nomSansSpace = tableauMots.join('');

        let modeTest = false;
        if (nomJoueur === "modeTest") {
            modeTest = true;
        }

        if (nomJoueur === "") {
            alert("Veuillez entrer un nom de joueur.");
        } else if (nomSansSpace.length > 12) {
            alert("La taille du nom ne doit pas excéder 12 caractères")
        } else if (!estNomValide(nomSansSpace)) {
            alert("Le nom de joueur ne doit contenir que des lettres et des chiffres.");
        } else if (joueurCount > 5) {
            alert("Vous avez atteint le nombre maximum de joueurs (6).");
        } else {
            for (let i = 0; i < nomsJoueurs.length; i++) {
                if (nomSansSpace === nomsJoueurs[i]){
                    alert("Ce nom existe déjà");
                    return;
                }
            }
            joueurCount++;
            nomsJoueurs.push(nomJoueur);
            const joueursContainer = document.getElementById('joueursContainer');
            const nouveauJoueur = document.createElement('div');
            nouveauJoueur.className = 'joueur';
            
            // Créer le bouton annuler avec l'image
            const btnAnnule = document.createElement('button');
            btnAnnule.className = 'btn-annule';

            const imgAnnule = document.createElement('img');
            imgAnnule.src = '../styles/images/moins.png'; // Remplacez par le chemin de votre image
            imgAnnule.alt = 'Annuler'; // Texte alternatif pour l'image
            imgAnnule.className = 'img-annule';

            // Ajouter l'image à l'intérieur du bouton
            btnAnnule.appendChild(imgAnnule);

            // Ajouter le bouton annuler à gauche du texte
            nouveauJoueur.appendChild(btnAnnule);

            // Ajouter le texte "Joueur" + joueurCount avec un conteneur div
            const texteJoueur = document.createElement('div');
            texteJoueur.className = 'numero'
            texteJoueur.textContent = 'Joueur ' + joueurCount;
            nouveauJoueur.appendChild(texteJoueur);

            // Ajouter le texte
            const nom = document.createElement('div');
            nom.className = 'nomJoueur';
            nom.innerHTML = nomSansSpace;
            nouveauJoueur.appendChild(nom);

            // Ajouter le joueur au conteneur
            joueursContainer.appendChild(nouveauJoueur);

            // Ajouter un gestionnaire d'événements au bouton -
            btnAnnule.addEventListener('click', supprimerJoueur);

            // Effacer le contenu de l'input après l'ajout
            inputNom.value = "";
            // Après avoir modifié nomsJoueurs, sauvegardez-le dans le stockage local
            sauvegarderNomsJoueurs();

            localStorage.setItem('modeTest', JSON.stringify(modeTest));
        }
    }

    const inputNom = document.getElementById('username');

    inputNom.addEventListener('keydown', function(event) {
        if (event.key === 'Enter') {
            // Si la touche "Entrée" est pressée, appeler la fonction ajouterJoueur()
            ajouterJoueur();
        }
    });

    // Ajout d'un gestionnaire d'événements au bouton
    const btnPlus = document.querySelector('.btn-plus');
    btnPlus.addEventListener('click', ajouterJoueur);

    // Vérifier la taille du tableau nomsJoueurs avant de permettre le lien
    document.getElementById('lienJouer').addEventListener('click', function(event) {    


        if (nomsJoueurs.length < 2) {
            // Si le tableau contient moins de deux joueurs, afficher une alerte
            alert("Il doit y avoir au moins deux joueurs pour jouer !");
            event.preventDefault(); // Empêcher le lien de se déclencher
        }
        // Sinon, le lien s'ouvrira normalement
    });
});