const dice_1 = "../styles/images/dice_1.png"
const dice_2 = "../styles/images/dice_2.png"
const dice_3 = "../styles/images/dice_3.png"
const dice_4 = "../styles/images/dice_4.png"
const dice_5 = "../styles/images/dice_5.png"
const dice_6 = "../styles/images/dice_6.png"


function getImageDice(i) {
    switch (i) {
        case 1 : return dice_1
        case 2 : return dice_2
        case 3 : return dice_3
        case 4 : return dice_4
        case 5 : return dice_5
        case 6 : return dice_6
    }
}

const modeTest = JSON.parse(localStorage.getItem('modeTest'));
const couleurs = ["#ff0000", "#19b825", "#0000ff", "#ec9414", "#f400d7", "#874e31"];
let dicoJoueurs = {}
let rejoue = false;
let nbTotalMaison = 32;
let nbTotalHotel = 12;
let pot_commun = 0;
let tabJoueurEnchere = [];
let sommeDesDes = 0


function ajoutDomModeTest() {
    // Pour afficher la case du joueur
    let parentDiv = document.querySelector('.tour > h1');
    // Element span
    let position = document.createElement('span');
    position.id = "position"
    position.textContent = " 0"
    // Ajout de span au parent
    parentDiv.appendChild(position);


    // Pour joueur ce qu'on veut
    parentDiv = document.querySelector('.info_jeux');
    let copyright = document.querySelector('.copyrigth');
    // Element Div
    let testDiv = document.createElement('div');
    testDiv.id = "test";
    testDiv.classList.add("mode_test");

    // Element form
    let testForm = document.createElement('form');
    testForm.id = "testForm";

    // Element input
    let testInput = document.createElement('input');
    testInput.id = "testInput";
    testInput.type = "text";

    // Element Button
    let testButton = document.createElement('input');
    testButton.id = "testButton";
    testButton.type = "submit";

    // Ajout Input au Form
    testForm.appendChild(testInput);

    // Ajout Button au Form
    testForm.appendChild(testButton);

    // Ajout Form au Div
    testDiv.appendChild(testForm);

    // Ajout Div au parent
    parentDiv.insertBefore(testDiv, copyright);

    // EventListener sur le submit
    testButton.addEventListener('click', function(event) {
        event.preventDefault();
        lancerDesModeTest();
    });
}


function lancerDesModeTest() {
    var inputDes = document.querySelector('#testInput');
    var valDes = inputDes.value;
    // var tableauDes = valDes.split(';'); // Divise la chaîne en deux valeurs
    mouvementPion(parseInt(valDes), 0);
}


function initPlateau(){
    classementJoueurs()
    if (modeTest) {ajoutDomModeTest(); }
    
    const btnSortirPrison = document.getElementById("btnSortirPrison");

    // Ajout d'un événement au survol de la souris
    btnSortirPrison.addEventListener("mouseover", function() {
        // Sélection du deuxième span
        const btnTextTwo = btnSortirPrison.querySelector('.btn-text-two');
        // Modification du texte du deuxième span
        if (dicoJoueurs[document.getElementById("nom_joueur").textContent].getFreedom()){
            btnTextTwo.textContent = "Libérer de Prison";
        } else {
            btnTextTwo.textContent = "Payer 50!";

        }
    });

    document.getElementById("lancer_des").addEventListener("click", lancer_aleatoire, false);
    document.getElementById("btnSortirPrison").addEventListener("click", sortirDePrison, false);
}

/**********************************************
Créer un pion HTML
**********************************************/

function creerPionsHtml(tabNomJoueurs){
    let parentDiv = document.querySelector('.pionsJeux');
    tabNomJoueurs.forEach((nomJoueur) => {
        let newDiv = document.createElement('div');
        newDiv.id = nomJoueur;
        parentDiv.appendChild(newDiv)
    });document.getElementById("lancer_des").disabled = true;
    document.getElementById("buttonSuivant").disabled = true;
}


/**********************************************
 Cette partie sert à animer et à tirer aléatoirement le numéro des dés
 **********************************************/
function lancer_aleatoire(){
    document.getElementById("lancer_des").disabled = true;
    document.getElementById("buttonSuivant").disabled = true;
    document.getElementById("btnSortirPrison").style.display = "none";

    animation_loaded(() => { // Passer lancer_aleatoire en tant que callback
        const de1 = Math.floor(Math.random() * 6) + 1;
        const de2 = Math.floor(Math.random() * 6) + 1;
        sommeDesDes = de1 + de2;
        const imageDice1 = getImageDice(de1);
        const imageDice2 = getImageDice(de2);
        const idDice1 = document.getElementById("de_1");
        const idDice2 = document.getElementById("de_2");
        idDice1.textContent = '';
        idDice2.textContent = '';

        const image1 = document.createElement('img');
        image1.src = imageDice1;
        const image2 = document.createElement('img');
        image2.src = imageDice2;

        idDice1.appendChild(image1);
        idDice2.appendChild(image2);

        mouvementPion(de1, de2)
        // mouvementPion(2, 2)
    });
}

    function animation_loaded(callback){
        const idDice1 = document.getElementById("de_1");
        const idDice2 = document.getElementById("de_2");

        for (let i = 1; i <= 6; i++) {
            setTimeout(function() {
                const imageDice = getImageDice(i);
                idDice1.innerHTML = '';
                idDice2.innerHTML = '';
                const image1 = document.createElement('img');
                image1.src = imageDice;
                const image2 = document.createElement('img');
                image2.src = imageDice;
                idDice1.appendChild(image1);
                idDice2.appendChild(image2);

                if (i === 6) { // Si c'est la dernière itération, appeler le callback
                    callback();
                }
            }, 80*i); // 80 millisecondes * i (augmenter le délai pour chaque itération)
        }
    }

function initDisplay(){
    document.getElementById('alertPrison').style.display = 'none';
    document.getElementById('alerteArgent').style.display = 'none';
    document.getElementById('alertePotCommun').style.display = 'none';
    document.getElementById('alerteTaxeLuxe').style.display = 'none';
    document.getElementById('alerteImpots').style.display = 'none';
    document.getElementById('alertePerdu').style.display = 'none';
    document.getElementById('alerteGagne').style.display = 'none';

    document.getElementById("buttonSuivant").disabled = true;
    document.getElementById('btnSortirPrison').style.display = 'none';
    document.getElementById("param_jeux_local").style.display = "none";
}

function putCSStoPlayer(nomJoueur, index) {
    // Générer le sélecteur CSS pour cet ID de joueur
    let selector = "#" + nomJoueur;
    // Sélectionner l'élément correspondant à l'ID du joueur
    let element = document.querySelector(selector);
    // Appliquer les règles CSS à l'élément sélectionné
    if (element) {
        // Calculer les coordonnées de la nouvelle position du pion
        let coodPion = calcIndicePion(dicoJoueurs[nomJoueur].getPosition(), index+1);
        // Appliquer les règles CSS à l'élément sélectionné
        element.style.position = "absolute";
        element.style.top = coodPion[0] + "%" // Position verticale
        element.style.left = coodPion[1] + "%"; // Position horizontale
        element.style.transform = "translate(-50%, -50%)";
        element.style.width = "3%";
        element.style.height = "3%";
        element.style.border = "1px solid #000";
        element.style.borderRadius = "50%";
        element.style.backgroundColor = couleurs[index];
    }
}

function sortirDePrison(){
    let nomJoueurElement = document.getElementById("nom_joueur");
    let idPion = nomJoueurElement.textContent;
    //Vérif si il a utiliser la carte sortie de prison
    if (document.querySelector('.btn-text-two').textContent === "Libérer de Prison"){
        //La remettre dans la bonne pile
        let freedom = dicoJoueurs[idPion].getFreedom();
        dicoJoueurs[idPion].suppFreedom();
        //Recup la bonne pioche
        let pioche = null;
        if (freedom.getUrl().includes("../styles/images/communaute")) {
            pioche = piocheCommunaute;
        } else {
            pioche = piocheChance;
        }
        freedom.remettreEnDessousDeLaPioche(pioche);
        //Replacer le joueur
        dicoJoueurs[idPion].setPosition(10)
        const newIndice = calcIndicePion(10)
        let pion = document.getElementById(idPion);
        pion.style.top = newIndice[0] + "%"; // Ajout des unités %
        pion.style.left = newIndice[1] + "%"; // Ajout des unités Lib%Lib
        //Supp l'affichage
        const elemSupp = document.querySelector(".freedom");
        elemSupp.removeChild(elemSupp.firstChild);
    } else if(dicoJoueurs[idPion].getSolde() >= 50){
        dicoJoueurs[idPion].setPosition(10);
        dicoJoueurs[idPion].suppSolde(50);
        //actualiser la position
        const newIndice = calcIndicePion(10);

        let pion = document.getElementById(idPion);
        pion.style.top = newIndice[0] + "%"; // Ajout des unités %
        pion.style.left = newIndice[1] + "%"; // Ajout des unités %
        //afficher le nouveau solde bancaire
        newSoldeBancaire(idPion);
    }
    else{
        //Alerte, vous n'avez pas assez d'argent
        //résoudre une énigme pour récupérer de l'argent ???
        document.getElementById('alerteArgent').style.display = 'block';
        rejoue = true;
        desactiverBoutons();
    }
    document.getElementById("btnSortirPrison").style.display = "none";
    if(mode === 'online'){
        let joueurs = Object.values(dicoJoueurs).map(joueur => joueur.getObject());
        let joueur = joueurs[indexJoueur]
        console.debug(joueur)
        socket.emit('finishMoving', roomID, joueur);
    }
}

function alerte_potCommun(argent){
    let idPotCommun = document.getElementById("argentPotCommun")
    idPotCommun.textContent = argent

    document.getElementById('alertePotCommun').style.display = 'block';
    desactiverBoutons()
}


/**********************************************
Partie qui sert à faire avancer un pion de (de1+de2) cases
**********************************************/
function mouvementPion(de1, de2){
    let nomJoueurElement = document.getElementById("nom_joueur");
    let idPion = nomJoueurElement.textContent;

    let currentPos = dicoJoueurs[idPion].getPosition()
    let nbCases = de1+de2
    let newPos = (currentPos+nbCases) % 40
    let coodPion = [0,0]

    let double = de1 === de2;
    rejoue = false;

    if(double){
        console.log("DOUBLE")
        if(currentPos != 40){  //le joueur n'est pas en prison
            //compter après le nombre de double dans 1 tour
            dicoJoueurs[idPion].incrCompteurDouble()

            if(dicoJoueurs[idPion].getcompteurDouble() == 3){
                //direction la prison
                dicoJoueurs[idPion].reinitCompteurDouble()
                newPos = 40  //pour qu'il aille en prison (animation meilleure)
                teleportationPion(newPos, idPion)
                return null  //pour aller directement en prison
            }
            else{   //le double compte normalement et le joueur peut rejouer
                rejoue = true
            }
        }
    }


    if(currentPos == 40){   //Si le joueur est en prison
        if(double || dicoJoueurs[idPion].getcompteurPrison() == 2){
            //Le joueur a fait un double donc il peut sortir de prison
            // ou alors c'est le troisième tour où il est en prison
            if(dicoJoueurs[idPion].getcompteurPrison() == 2 && !double){
                //il sort mais il doit payer 50
                if(dicoJoueurs[idPion].getSolde() < 50){
                    document.getElementById("alertePerdu").style.display = "block";
                    desactiverBoutons();
                    newPos = currentPos;
                    return;
                }
                else{
                    dicoJoueurs[idPion].suppSolde(50)
                    newSoldeBancaire(idPion)
                }
            }

            newPos = 10 + nbCases
            currentPos = 10
            dicoJoueurs[idPion].reinitCompteurPrison()
            coodPion = calcIndicePion(newPos, dicoJoueurs[idPion].getNumero())
        }
        else{
            dicoJoueurs[idPion].incrCompteurPrison()
            newPos = currentPos;
            coodPion = calcIndicePion(newPos, dicoJoueurs[idPion].getNumero());
        }
    }
    else{
        coodPion = calcIndicePion(newPos, dicoJoueurs[idPion].getNumero())
    }

    faireAvancerPion(idPion, currentPos, newPos, coodPion, true)

    if (modeTest){
        // Affichage de la position du pion pour le mode de test
        // On ajoute la position du pion du joueur à coté de son nom
        let position = document.querySelector('#position')
        position.textContent = " " + newPos;
    }
}

/**********************************************
Partie qui sert à faire avancer un pion quand on tire une carte Chance ou Caisse de Communauté
**********************************************/
function deplacementPionTirageCarte(newPos, idPion, avancer){
    desactiverBoutons()
    let currentPos = dicoJoueurs[idPion].getPosition()
    let coodPion = [0,0]

    coodPion = calcIndicePion(newPos, dicoJoueurs[idPion].getNumero())

    faireAvancerPion(idPion, currentPos, newPos, coodPion, avancer)
}

/**********************************************
Déplacer un pion par incréments
**********************************************/
function faireAvancerPion(idPion, currentPos, newPos, coodPion, avancer){
    //faire l'affichage progressif
    progressifPion(currentPos, newPos, idPion, avancer, dernierPosition)

    //Passer tout ce qu'il reste en tant que fonction de callback de progressifPion
    function dernierPosition(){
        //si le joueur s'arrête sur la case prison
        if(newPos == 30){
            document.getElementById('alertPrison').style.display = 'block';
            desactiverBoutons();
            //le joueur va en prison
            newPos = 40
            coodPion[0] = 91
            coodPion[1] = 9
            rejoue = false
            dicoJoueurs[idPion].reinitCompteurPrison()  //on réinitialise le compteur de double => déjà en prison
        }
        else if(newPos == 38){  //taxe de luxe
            //le joueur paye 100
            dicoJoueurs[idPion].suppSolde(100)
            newSoldeBancaire(idPion)
            //afficher une alerte a l'écran
            document.getElementById('alerteTaxeLuxe').style.display = 'block';
            desactiverBoutons()
        }
        else if(newPos == 4){   //Impôts sur le revenu
            //le joueur paye 200
            dicoJoueurs[idPion].suppSolde(200)
            newSoldeBancaire(idPion)
            pot_commun += 200
            //afficher une alerte a l'écran
            document.getElementById('alerteImpots').style.display = 'block';
            console.log("CA BLOQUE !! PD")
            desactiverBoutons();
        }
        else if(newPos == 20){  //Parc gratuit
            //le joueur récupère l'argent du pot commun
            dicoJoueurs[idPion].addSolde(pot_commun)
            newSoldeBancaire(idPion)
            
            //afficher une alerte à l'écran
            if(pot_commun > 0){
                alerte_potCommun(pot_commun)
            }

            pot_commun = 0
        }
        
        //Modifier le css pour idPion
        let pion = document.getElementById(idPion)
        pion.style.top = coodPion[0] + "%"; // Ajout des unités %
        pion.style.left = coodPion[1] + "%"; // Ajout des unités %
        
        //afficher la carte correspondante
        afficheCarte(newPos);

        dicoJoueurs[idPion].setPosition(newPos) //mettre a jour la position dans la classe Joueur
        if(mode === 'online'){
            let joueurs = Object.values(dicoJoueurs).map(joueur => joueur.getObject());
            let joueur = joueurs[indexJoueur]
            console.debug(joueur)
            socket.emit('finishMoving', roomID, joueur);
        }

        if ((document.getElementById("cartePasProprio").style.display === "none" || document.getElementById("cartePasProprio").style.display === "") &&
            (document.getElementById("carteProprio").style.display === "none" || document.getElementById("carteProprio").style.display === "")) {
            if (document.querySelector(".chanceCommunaute").style.display === "block"){
                //Pass
            } else {
                reactiverBoutons(idPion);
            }
        }
    }
}

function progressifPion(currentPos, newPos, idPion, avancer, callback) {
    const nbCases = 40; // Nombre total de cases sur le plateau de Monopoly

    // Déterminer l'ordre de parcours des cases
    let startIndex = currentPos; // Parce que le pion est déjà sur currentPos
    let endIndex = newPos;

    if(avancer){
        //normalement, end > start
        if (endIndex < startIndex) {
            endIndex += nbCases;
        }
    }
    else{
        //normalement, start > end
        if(startIndex < endIndex){
            startIndex += nbCases;
        }
    }
    
    // Parcourir circulairement les cases du plateau
    let i = 0;
    if(avancer){
        i = startIndex + 1;
    }
    else{
        i = startIndex - 1;
    }
    function loop() {
        const caseIndex = i % nbCases; // Utiliser l'opérateur modulo pour assurer la circularité
        // Affichage du nombre
        let coodPion = [0, 0];
        coodPion = calcIndicePion(caseIndex, dicoJoueurs[idPion].getNumero());

        if(caseIndex==0){
            //le joueur passe par la case départ
            dicoJoueurs[idPion].incrNbTours()
            console.log(idPion, " est a son tour n° ", dicoJoueurs[idPion].getNbTours())
            console.log("Il prend 200$")
            dicoJoueurs[idPion].addSolde(200)
            newSoldeBancaire(idPion)
        }

        let pion = document.getElementById(idPion);
        pion.style.top = coodPion[0] + "%"; // Ajout des unités %
        pion.style.left = coodPion[1] + "%"; // Ajout des unités %

        // Incrémentation pour passer à la prochaine case
        if(avancer){
            i++;
        }
        else{
            i--;
        }


        // Vérifier si nous sommes arrivés à la dernière case
        if(avancer){
            if (i <= endIndex) {
                setTimeout(loop, 250);
            } else {
                console.log("Fin du déplacement du pion");
                callback();
            }
        }
        else{
            if (i >= endIndex) {
                setTimeout(loop, 250);
            } else {
                console.log("Fin du déplacement du pion");
                callback();
            }
        }
        
    }
    loop(); // Démarrer la boucle
}


//Fonction qui calcule la nouvelle position d'un pion sur le plateau
function calcIndicePion(newPos, numeroJoueur){
    let coodPion = [0,0] 

    if(newPos >= 1 && newPos <= 9){
        coodPion[0] = 95
        coodPion[1] = 82 - 8*(newPos-1)
    }
    else if(newPos >= 11 && newPos <= 19){
        coodPion[0] = 82 - 8*(newPos-11)
        coodPion[1] = 5
    }
    else if(newPos >= 21 && newPos <= 29){
        coodPion[0] = 5
        coodPion[1] = 18 + 8*(newPos-21)
    }
    else if(newPos >= 31 && newPos <= 39){
        coodPion[0] = 18 + 8*(newPos-31)
        coodPion[1] = 95
    }

    else if(newPos == 0){        //donner 400$ au joueur
        coodPion[0] = 93
        coodPion[1] = 93
    }
    else if(newPos == 10){
        coodPion[0] = 97.5
        coodPion[1] = 2.5
    }
    else if(newPos == 20){    //récupérer l'argent du début
        coodPion[0] = 7
        coodPion[1] = 7
    }
    else if(newPos == 30){    //Aller en prison => newPos = 40
        coodPion[0] = 7
        coodPion[1] = 93
    }
    else if (newPos == 40){
        coodPion[0] = 91
        coodPion[1] = 9
    }
    // PROBLEME : différencier les cas où les pions sont sur des cases horizontales ou verticales
    // Ajuste la position des pions pour qu'ils ne se superposent pas
    if (numeroJoueur < 3) {
        //coodPion[0] -= (numeroJoueur -1) ; // Adjust top position
        //coodPion[1] -= (numeroJoueur - 1); // Adjust left position
    }
    else if (numeroJoueur > 3) {
        //coodPion[0] += (numeroJoueur -1) ; // Adjust top position
        //coodPion[1] += (numeroJoueur - 1); // Adjust left position
    }
    return coodPion
}


//Fonction qui prend en entrée une position et affiche la carte et les boutons correspondant
function afficheCarte(cartePosition){
    let carteImg = document.getElementById("carteImg");
    let isCarte = false;
    // Vérifie si la position de la carte existe dans le dictionnaire
    if (carteDict.hasOwnProperty(cartePosition) || gareDict.hasOwnProperty(cartePosition) || compagnieDict.hasOwnProperty(cartePosition)) {
        isCarte = true
        document.querySelector(".btn-maison").disabled = true;
        var dict;
        if (carteDict.hasOwnProperty(cartePosition)){
            dict = carteDict;
        } else if (gareDict.hasOwnProperty(cartePosition)){
            dict = gareDict;
        } else {
            dict = compagnieDict;
        }
        // Remplace la source de l'image par l'URL de la carte
        carteImg.src = dict[cartePosition].getUrl();
        // carteImg.style.display = "block";
        document.getElementById("carte").style.display = "block";

        if (dict[cartePosition].getProprietaire()){
            var proprio = document.querySelector(".Proprio").style.display = "block";
            var bouttonPayer = document.querySelector(".payer");
            // Il faudrait vérifier que le proprio est bien différent de celui qui est tomber sur la carte
            if (dict[cartePosition].getProprietaire() === dicoJoueurs[document.getElementById("nom_joueur").textContent]){
                bouttonPayer.textContent = "Bienvenue chez vous";

            } else {
                bouttonPayer.textContent = "Payer " + dict[cartePosition].calculLoyer() + "$ à " + dict[cartePosition].getProprietaire().getNom();
            }
        } else {
            var proprio = document.querySelector(".pasProprio");
            proprio.style.display = "block";
            var bouttonAcheter = document.querySelector(".acheter");
            bouttonAcheter.textContent = "Acheter pour " + dict[cartePosition].getPrix() + "$"; 
        }

    } else {
        // Carte chance ou caisse de communauté
        document.getElementById("buttonSuivant").disabled = true;
        document.querySelector(".btn-maison").disabled = true;
        if (cartePosition === 2 || cartePosition === 17 || cartePosition === 33){
            isCarte = true
            //communauté
            let carteTiree = tirerCarte(piocheCommunaute);
            // Affichahge de la carte
            let carteImg = document.getElementById("carteImg");
            // Remplace la source de l'image par l'URL de la carte
            carteImg.src = carteTiree.getUrl();
            // carteImg.style.display = "block";
            var carte = document.getElementById("carte");
            carte.style.display = "block";
            // Traitement de la carte avec affichage des boutons correspondant
            carteTiree.traiteCarte();
            // Remettre la carte tirée en dessous de la pioche
            if (!carteTiree.getFreedom()){
                carteTiree.remettreEnDessousDeLaPioche(piocheCommunaute);
            } //On ne remet pas la carte sortie de prison avant qu'elle soit utilisé
        } else if (cartePosition === 7 || cartePosition === 22 || cartePosition === 36){
            isCarte = true
            //chance
            // On bloque le bouton
            
            let carteTiree = tirerCarte(piocheChance);
            // Affichahge de la carte
            let carteImg = document.getElementById("carteImg");
            // Remplace la source de l'image par l'URL de la carte
            carteImg.src = carteTiree.getUrl();
            // carteImg.style.display = "block";
            var carte = document.getElementById("carte");
            carte.style.display = "block";
            // Traitement de la carte avec affichage des boutons correspondant
            carteTiree.traiteCarte();
            // Remettre la carte tirée en dessous de la pioche
            if (!carteTiree.getFreedom()){
                carteTiree.remettreEnDessousDeLaPioche(piocheChance);
            } //On ne remet pas la carte sortie de prison avant qu'elle soit utilisé
        } else {
            // case simplevisite...
        }
    }

    return isCarte;
}

function closeAlertPrison() {
    document.getElementById('alertPrison').style.display = 'none';
    const nomJoueur = document.getElementById("nom_joueur").textContent;
    reactiverBoutons(nomJoueur)
    
}

function closeAlerteArgent(){
    document.getElementById('alerteArgent').style.display = 'none';
    const nomJoueur = document.getElementById("nom_joueur").textContent;
    reactiverBoutons(nomJoueur)
}

function closeAlertePotCommun(){
    document.getElementById('alertePotCommun').style.display = 'none';
    const nomJoueur = document.getElementById("nom_joueur").textContent;
    reactiverBoutons(nomJoueur)
}

function closeAlerteTaxeLuxe(){
    document.getElementById('alerteTaxeLuxe').style.display = 'none';
    const nomJoueur = document.getElementById("nom_joueur").textContent;
    reactiverBoutons(nomJoueur)
}

function closeAlerteImpots(){
    document.getElementById('alerteImpots').style.display = 'none';
    const nomJoueur = document.getElementById("nom_joueur").textContent;
    reactiverBoutons(nomJoueur)
}

function reactiverBoutons(idPion){
    if(document.getElementById('alertPrison').style.display == 'block'
    || document.getElementById('alerteArgent').style.display == 'block'
    || document.getElementById('alertePotCommun').style.display == 'block'
    || document.getElementById('alerteTaxeLuxe').style.display == 'block'
    || document.getElementById('alerteImpots').style.display == 'block'
    || document.getElementById('alertePerdu').style.display == 'block'){

        document.getElementById("buttonSuivant").disabled = true;
        document.getElementById("lancer_des").disabled = true;
        return;
    }

    if (rejoue){
        document.getElementById("lancer_des").disabled = false;
        document.getElementById("buttonSuivant").disabled = true;
    }
    else{
        dicoJoueurs[idPion].reinitCompteurDouble()
        if(document.getElementById('alertPrison').style.display == 'block'
        || document.getElementById('alerteArgent').style.display == 'block'
        || document.getElementById('alertePotCommun').style.display == 'block'
        || document.getElementById('alerteTaxeLuxe').style.display == 'block'
        || document.getElementById('alerteImpots').style.display == 'block'
        || document.getElementById('alertePerdu').style.display == 'block'
        || document.getElementById('alerteGagne').style.display == 'block'){

            document.getElementById("buttonSuivant").disabled = true;

        }
        else{
            document.getElementById("buttonSuivant").disabled = false;
            document.getElementById("lancer_des").disabled = true;
            document.querySelector(".btn-maison").disabled = false;
        }
    }
}

function desactiverBoutons(){
    document.getElementById("lancer_des").disabled = true;
    document.getElementById("buttonSuivant").disabled = true;
    document.getElementById("carte").style.display = "none";
}

/**************************************************************************
    Partie qui sert à gérer les boutons quand une carte s'affiche à l'écran
**************************************************************************/

//Fonction gérant le cas si un joueur décide d'acheter une carte
function acheterPropriete(){
    const nomJoueur = document.getElementById("nom_joueur").textContent;
    let cartePosition = dicoJoueurs[nomJoueur].getPosition();
    let casePlateau;
    
    let dict = null;
    let classe = null;
    if (carteDict.hasOwnProperty(cartePosition)){
        dict = carteDict;
    } else if (gareDict.hasOwnProperty(cartePosition)){
        dict = gareDict;
        classe = "gare"
    } else {
        dict = compagnieDict;
        classe = "compagnie"
    }
    casePlateau = dict[cartePosition];
    
    const prix = casePlateau.getPrix();
    const solde = document.getElementById("solde_compte");
    if (solde.textContent < prix){
        alert("Vous n'avez pas assez d'argent veuillez mettre la propriété aux enchères !");
        return
    }
    // Ca c'est juste visuel
    solde.textContent -= prix; 
    // Ca c'est ce qui sera sauvegardé pour le prochain tour
    dicoJoueurs[nomJoueur].suppSolde(prix)
    newSoldeBancaire(nomJoueur) //Actualiser le solde bancaire du joueur

    if (carteDict.hasOwnProperty(cartePosition)){
        dicoJoueurs[nomJoueur].ajouterPropriete(casePlateau.getCouleur(), casePlateau);
        if (casePlateau.getCouleur() === "bleu clair"){
            classe = "bleuClair"
        }
        else if (casePlateau.getCouleur() === "bleu foncé"){
            classe = "bleuFonce"
        } else {
            classe = casePlateau.getCouleur();
        }
    } else if (gareDict.hasOwnProperty(cartePosition)){
        dicoJoueurs[nomJoueur].ajouterGare(casePlateau);
    } else if (compagnieDict.hasOwnProperty(cartePosition)){
        dicoJoueurs[nomJoueur].ajouterCompagnie(casePlateau);
    } else {
        console.log('erreur : carte non supportée')
    }
    

    //il faut ajouter le couleur du pion sur la carte et afficher la carte
    ajouterPionSurCarte(dict, nomJoueur, cartePosition, null);

    ajouterCarte(classe, casePlateau);
    // On met tout en none pour continuer à jouer
    let carte = document.getElementById("carte");
    carte.style.display = "none";
    let proprio = document.querySelector(".pasProprio");
    proprio.style.display = "none";

    reactiverBoutons(nomJoueur);
}


//Fonction qui initialise le form d'enchere et l'affiche
function enchere(){
    // On met tout en none pour continuer à jouer
    // Verifier si il ne reste qu'un joueur dans l'enchère 
    let carte = document.getElementById("carte");
    carte.style.display = "none";
    let proprio = document.querySelector(".pasProprio");
    proprio.style.display = "none";

    if(mode === 'online'){
        const nomJoueur = document.getElementById("nom_joueur").textContent;
        let joueur = Object.keys(dicoJoueurs);
        let joueurSuivant;

        for (let i = 0; i < joueur.length; i++) {
            if (joueur[i] === nomJoueur) {
                joueur.splice(i, 1); // Supprime le joueur courant
                joueurSuivant = joueur[i] || joueur[0]; // Récupère le joueur suivant ou le premier joueur si c'était le dernier
                break;
            }
        }
        console.debug(joueur, joueurSuivant);
        socket.emit('showSales', roomID, joueur, joueurSuivant, 0, '#FFFFFF');
    } else {
        // On initialise le champ pseudo
        const nomJoueur = document.getElementById("nom_joueur").textContent;
        for (let cle in dicoJoueurs) {
            // Accéder au joueur associé à chaque clé
            if (cle !== nomJoueur){
                //Si vrai il faut ajouter le nom du joueur dans le champ pseudo
                let selectElement = document.getElementById("pseudo");
                // Créer un nouvel élément <option>
                let optionElement = document.createElement("option");
                optionElement.value = cle;
                optionElement.textContent = cle;
                selectElement.appendChild(optionElement);
            }
        }
        // On affiche le form des enchere
        document.querySelector(".login-box").style.display = "block";
    }

}

//Fonction qui efface le form d'enchere
function annuleEnchere(){
    if (mode === 'online'){
        // Supprimer le joeuurs de l'enchère
        let joueurSuivant;
        for (let i = 0; i < tabJoueurEnchere.length; i++) {
            if (tabJoueurEnchere[i] === username) {
                joueurSuivant = tabJoueurEnchere[i + 1] || tabJoueurEnchere[0]; // Récupère le joueur suivant ou le premier joueur si c'était le dernier
                break;
            }
        }
        console.log("le joueur suivant veut quitter l'enchère : " + username)
        tabJoueurEnchere = tabJoueurEnchere.filter(item => item !== username)
        
        let valeur = document.getElementById("prixEnchere").textContent
        let couleur = document.getElementById("prixEnchere").style.color
        socket.emit('showSales', roomID, tabJoueurEnchere, joueurSuivant, valeur, couleur);
    } else {
        //On désactive le form à la fin
        document.querySelector(".login-box").style.display = "none";
        let carte = document.getElementById("carte");
        carte.style.display = "block";
        let proprio = document.querySelector(".pasProprio");
        proprio.style.display = "block";
        // on remet les champs vide
        let selectElement = document.getElementById("pseudo");
    
        // Boucle pour supprimer tous les éléments enfants sauf le premier
        for (let i = selectElement.options.length - 1; i > 0; i--) {
            selectElement.remove(i);
        }
        document.getElementById("price").value = "";
    }
}

//Fonction qui traite le form d'enchere et la transaction
function validateForm(){
    if (mode === 'online'){
        let joueurSuivant;
        for (let i = 0; i < tabJoueurEnchere.length; i++) {
            if (tabJoueurEnchere[i] === username) {
                joueurSuivant = tabJoueurEnchere[i + 1] || tabJoueurEnchere[0]; // Récupère le joueur suivant ou le premier joueur si c'était le dernier
                break;
            }
        }
        let valeur = document.getElementById("price").value;
        if (parseInt(valeur) <= parseInt(document.getElementById("prixEnchere").innerHTML)) {
            alert("La valeur renseigné est inférieur à la valeur courante")
            return
        }
        if (!parseInt(valeur)){
            alert("Veuillez renseigné une valeur ou vous coucher")
            return
        }
        let couleur = dicoJoueurs[username].getCouleur();
        socket.emit('showSales', roomID, tabJoueurEnchere, joueurSuivant, valeur, couleur);
    } else {
        let pseudo = document.getElementById("pseudo").value;
        let price = document.getElementById("price").value;

        // Vérifier que les champs ne sont pas vides
        if (pseudo.trim() === "" || price.trim() === "") {
            alert("Veuillez remplir tous les champs.");
            return;
        }

        const nomJoueur = document.getElementById("nom_joueur").textContent;

        // Vérifier que le pseudo est bien dans le dictionnaire et que ce n'est pas le joueur qui l'a mis aux encheres
        if (dicoJoueurs.hasOwnProperty(pseudo)) {
            if (pseudo === nomJoueur){
                alert("Le pseudo renseigné ne peux pas participer aux enchères")
                return
            }
        } else {
            alert("Le pseudo renseigné n'existe pas")
            return;
        }

        // Faire les bons appels à suppSolde et ajouter la bonne carte
        if (dicoJoueurs[pseudo].getSolde() < parseInt(price)){
            alert(pseudo + " ne possède pas assez d'argent pour acheter cette carte")
            return
        } else if (parseInt(price) < 0){
            alert("Veuillez rentrer une valeur positive");
            return
        }

        dicoJoueurs[pseudo].suppSolde(parseInt(price));

        let cartePosition = dicoJoueurs[nomJoueur].getPosition();

        let casePlateau;

        let dict;
        let classe = null;

        if (carteDict.hasOwnProperty(cartePosition)){
            dict = carteDict;
        } else if (gareDict.hasOwnProperty(cartePosition)){
            dict = gareDict;
        } else {
            dict = compagnieDict;
        }
        casePlateau = dict[cartePosition];

        if (carteDict.hasOwnProperty(cartePosition)){
            dicoJoueurs[pseudo].ajouterPropriete(casePlateau.getCouleur(), casePlateau);
        } else if (gareDict.hasOwnProperty(cartePosition)){
            dicoJoueurs[pseudo].ajouterGare(casePlateau);
        } else {
            dicoJoueurs[pseudo].ajouterCompagnie(casePlateau);
        }


        //On désactive le form à la fin
        document.querySelector(".login-box").style.display = "none";

        // on remet les champs vide
        let selectElement = document.getElementById("pseudo");

        // Boucle pour supprimer tous les éléments enfants sauf le premier
        for (let i = selectElement.options.length - 1; i > 0; i--) {
            selectElement.remove(i);
        }    document.getElementById("price").value = "";
        newSoldeBancaire(nomJoueur);

        //il faut ajouter le couleur du pion sur la carte et la carte
        ajouterPionSurCarte(dict, pseudo, cartePosition, null);

        reactiverBoutons(nomJoueur);
    }
}



//Fonction qui paye le loyer d'un joueur à l'autre
function payerLoyer(){
    const nomJoueur = document.getElementById("nom_joueur").textContent;

    if (document.querySelector(".payer").textContent === "Bienvenue chez vous"){ // vérifier si le texte du bouton es tgenre bienvenue chez vous
        // On met tout en none pour continuer à jouer
        let carte = document.getElementById("carte");
        carte.style.display = "none";
        let proprio = document.querySelector(".Proprio");
        proprio.style.display = "none";
        reactiverBoutons(nomJoueur);
        return
    }
    let cartePosition = dicoJoueurs[nomJoueur].getPosition();
    let casePlateau;
    
    let dict;
    if (carteDict.hasOwnProperty(cartePosition)){
        dict = carteDict;
    } else if (gareDict.hasOwnProperty(cartePosition)){
        dict = gareDict;
    } else {
        dict = compagnieDict;
    }
    casePlateau = dict[cartePosition];
    const loyer = casePlateau.calculLoyer();
    const beneficiaire = casePlateau.getProprietaire();
    const solde = document.getElementById("solde_compte");

    if (solde.textContent < loyer){
        document.getElementById("alertePerdu").style.display = "block";
        desactiverBoutons()
        return
    }
    

    // Ca c'est juste visuel
    solde.textContent -= loyer; 
    // Ca c'est ce qui sera sauvegardé pour le prochain tour
    dicoJoueurs[nomJoueur].suppSolde(loyer)
    beneficiaire.addSolde(loyer)

    console.log("Nom joueur : ", nomJoueur);
    console.log("Nom bénéficiaire : ", beneficiaire);

    newSoldeBancaire(nomJoueur);

    // On met tout en none pour continuer à jouer
    let carte = document.getElementById("carte");
    carte.style.display = "none";
    let proprio = document.querySelector(".Proprio");
    proprio.style.display = "none";

    reactiverBoutons(nomJoueur);
}


function ajouterPionSurCarte(dicoCarte, idPion, numCarte, couleur){
    const propriete = dicoCarte[numCarte]
    let cood = propriete.getCoodPionProprietaire();
    let couleurPion = null;
    if (couleur){
        couleurPion = couleur;
    } else {
        couleurPion = dicoJoueurs[idPion].getCouleur();
    }

    //Créer le div dans le HTML
    let parentDiv = document.querySelector('.pionsProprietes');
    let newDiv = document.createElement('div');
    const nameDiv = idPion + '_' + numCarte;
    newDiv.id = nameDiv;
    parentDiv.appendChild(newDiv)

    //Modifier ses règles CSS
    let selector = "#" + nameDiv;
    let element = document.querySelector(selector);
    if (element) {
        element.style.position = "absolute";
        element.style.top = cood[0] + "%";
        element.style.left = cood[1] + "%";
        element.style.transform = "translate(-50%, -50%)";
        element.style.width = "1.5%";
        element.style.height = "1.5%";
        element.style.border = "1px solid #000";
        element.style.borderRadius = "50%";
        element.style.backgroundColor = couleurPion;
    }
}

// La fonction ajoute la carte à gauche mais il faut quand on change de joueur tout réinitialiser et mettre les nouvelles
function ajouterCarte(classe, carte){
    const carteElement = document.querySelector(".info_glob");
    const classOk = carteElement.querySelector("." + classe);
    // créer l'élément image et l'ajouter en fils à classOk
    const imageElement = document.createElement("img");

    // Attribution de la source à l'élément image
    imageElement.src = carte.getUrl();
    classOk.appendChild(imageElement);
}


//**************************** 
//Il faut ajouter une fonction qui initialise les pions si on recharge la page
//****************************



/*******************************************************
    Partie qui sert à gérer l'ajout de maison ou d'hôtel
*******************************************************/


let aLance = null;
//Fonction qui vérifie si un joueur peu en effet ajouter des maisons ou non et affiche le form
function addMaison(){
    console.log("Fonction ajout maison");
    document.getElementById("buttonSuivant").disabled = true;

    // on initialise le champ propriété en vérifiant si il a toutes les propriétés d'une catégorie
    const nomJoueur = document.getElementById("nom_joueur").textContent;
    const proprietes = dicoJoueurs[nomJoueur].getPropriete();
    let flag = false;
    for (let cle in proprietes) {
        // Accéder au tableau associé à chaque clé
        let tableauAssocie = proprietes[cle];
        if (tableauAssocie.length === tableauAssocie[0].getNbLot()){
            flag = true
            //Si vrai il faut ajouter le nom des propriétés dans le champ propriétés
            let selectElement = document.getElementById("propriete");
            for (let i = 0; i < tableauAssocie.length; i++) {
                const nom = tableauAssocie[i].getNom();
                // Créer un nouvel élément <option>
                let optionElement = document.createElement("option");
                optionElement.value = nom;
                optionElement.textContent = nom;
                selectElement.appendChild(optionElement);
            }
        }
    }
    // Si le champ propriété est vide ne pas afficher le form et renvoyer une erreur
    if (flag){
        // on affiche le form après avoir vérifié si le joueur peut ajouter des maisons ou hôtels
        if (document.getElementById("lancer_des").disabled === false){
            document.getElementById("lancer_des").disabled = true;
            aLance = true;
        } else {
            aLance = false;
        }

        document.querySelector(".maison-box").style.display = "block";

    } else {
        alert("Vous n'avez pas toutes les propriétés d'une même couleur vous ne pouvez donc pas ajouter de maisons ou hôtel")
        if (document.getElementById("lancer_des").disabled === true){
            reactiverBoutons(nomJoueur);
        }
    }
}

//Fonction qui calcule le cout des maisons et des hôtels
function calculateTotal(){
    //modification du prix à payer en temps réel
    let propriete = document.getElementById("propriete").value;
    let maison = document.getElementById("maison").value;
    let hotel = document.getElementById("hotel").value;
    let total;
    if (propriete === "" || maison === "" || hotel === "") {
        total = 0;
    } else {
        // Récup la propriété
        const nomJoueur = document.getElementById("nom_joueur").textContent;
        const proprieteRecup = trouverPropriete(nomJoueur, propriete);
        if (proprieteRecup === null){
            console.log("Pb sur la recherche de propriété")
        }
        // Vérif que les champs sont bien initialisé avec des valeurs plausible
        if (parseInt(maison) < 0 || parseInt(maison) > 4){
            alert("Veuillez renseigné des valeurs plausible pour les maisons")
            document.getElementById("maison").value = "";
            return
        } else if (parseInt(maison) > nbTotalMaison || parseInt(hotel) > nbTotalHotel) {
            if (parseInt(maison) > nbTotalMaison){
                alert("Il ne reste plus que " + nbTotalMaison + " maison(s)");
                document.getElementById("maison").value = nbTotalMaison;
                return
            } else {
                alert("Il ne reste plus d'hôtel")
                document.getElementById("hotel").value = "0";
                return
            }
        } else if (parseInt(maison) + proprieteRecup.getNombreMaison() > 4){
            alert("Vous avez déjà " + proprieteRecup.getNombreMaison() + " maison(s) sur cette propriétés vous pouvez en avoir maximum 4")
            document.getElementById("maison").value = "";
            return
        }
        if (parseInt(hotel) < 0 || parseInt(hotel) > 1){
            alert("Veuillez renseigné des valeurs plausible pour l'hôtel")
            document.getElementById("hotel").value = "";
            return
        } else if (parseInt(hotel) === 1 && proprieteRecup.getNombreMaison() + parseInt(maison) !== 4){
            alert("Il vous faut 4 maisons pour pouvoir ajouter un hôtel")
            document.getElementById("hotel").value = "";
            return
        }
        // Récup le prix hotel et maison puis ajouter dans le calcul
        const prixMaison = proprieteRecup.getPrixMaison();
        const prixHotel = proprieteRecup.getPrixHotel();
        total = (parseInt(maison) * prixMaison) + (parseInt(hotel) * prixHotel); //C'est pas ça encore le calcul
    }

    
    // Mise à jour du texte du bouton
    let button = document.getElementById('validateForm');
    button.textContent = "Payer " + total + "$";
    
    return total;
}

/*Fonction renvoyant une CasePropriete
    entrée : le nom d'un joueur et le nom de la propriété chercher
    sortie : la CasePropriete si le joueur est bien propriétaire de celle ce
*/
function trouverPropriete(nomJoueur, nomPropriete){
    const proprietes = dicoJoueurs[nomJoueur].getPropriete();
    for (let cle in proprietes) {
        // Accéder au tableau associé à chaque clé
        let tableauAssocie = proprietes[cle];
        if (tableauAssocie.length === tableauAssocie[0].getNbLot()){
            //Si vrai il faut trouver le nom des propriétés dans le champ propriétés
            for (let i = 0; i < tableauAssocie.length; i++) {
                if (tableauAssocie[i].getNom() === nomPropriete){
                    return tableauAssocie[i];
                }
            }
        }
    }
    return null
}

//Fonction validant le formulaire pour ajouter une maison
function validateFormMaison(){
    // on vérifie que tous les champs sont remplis
    let propriete = document.getElementById("propriete").value;
    let maison = document.getElementById("maison").value;
    let hotel = document.getElementById("hotel").value;

    if (propriete === "" || maison === "" || hotel === "") {
        alert("Veuillez remplir tous les champs.");
        return;
    }

    
    // on traite la transaction en ajoutant nbhotel nbmaison a propriete et joueur
    let total = calculateTotal();

    // Vérifier si le solde est suffisant et l'enlever et actualiser
    const nomJoueur = document.getElementById("nom_joueur").textContent;
    if (dicoJoueurs[nomJoueur].getSolde() < total){
        alert("Vous n'avez pas assez d'argent !")
        return
    }
    dicoJoueurs[nomJoueur].suppSolde(total);
    newSoldeBancaire(nomJoueur);

    // Ajouter à la propriété le nouveau nombre
    let proprieteRecup = trouverPropriete(nomJoueur, propriete);
    proprieteRecup.setNbMaison(parseInt(maison));
    proprieteRecup.setNbHotel(parseInt(hotel));
    
    // Ajouter au joueur le nouveau nombre
    dicoJoueurs[nomJoueur].setNbMaison(parseInt(maison));
    dicoJoueurs[nomJoueur].setNbHotel(parseInt(hotel));

    // Enlever les maisons à la variables globales
    nbTotalMaison -= parseInt(maison);
    // Si il y a un hotel rajouter 4 maison eet supp 1 hotel

    let pastille = document.getElementById(nomJoueur + "_" + proprieteRecup.getNumeroCase());
    if(parseInt(hotel) === 1){
        nbTotalHotel -= 1;
        nbTotalMaison += 4;
        pastille.textContent = "5";
    } else {
        if (pastille.textContent === ""){
            pastille.textContent = maison;
        } else {
            pastille.textContent = parseInt(pastille.textContent) + parseInt(maison);
        }
    }
    //On désactive le form à la fin
    document.querySelector(".maison-box").style.display = "none";

    // on remet les champs vide
    let selectElement = document.getElementById("propriete");

    // Boucle pour supprimer tous les éléments enfants sauf le premier
    for (let i = selectElement.options.length - 1; i > 0; i--) {
        selectElement.remove(i);
    }
    document.getElementById("maison").value = "";
    document.getElementById("hotel").value = "";
    // on réactive les boutons si le bouton lancer joueur est bloque
    if (aLance){
        document.getElementById("lancer_des").disabled = false;
    } else {
        reactiverBoutons(nomJoueur);
    }
}

//Fonction enlevant l'affichage du form pour ajouter des maisons
function annuleMaison(){
    //On désactive le form à la fin
    document.querySelector(".maison-box").style.display = "none";
    
    // on remet les champs vide
    let selectElement = document.getElementById("propriete");

    // Boucle pour supprimer tous les éléments enfants sauf le premier
    for (let i = selectElement.options.length - 1; i > 0; i--) {
        selectElement.remove(i);
    }
    document.getElementById("maison").value = "";
    document.getElementById("hotel").value = "";
    // on réactive les boutons si le bouton lancer joueur est bloque
    if (aLance){
        document.getElementById("lancer_des").disabled = false;
    } else {
        const nomJoueur = document.getElementById("nom_joueur").textContent
        reactiverBoutons(nomJoueur);
    }
}


function teleportationPion(newPos, nomJoueur){
    let classNomJoueur = dicoJoueurs[nomJoueur];
    let currentPos = classNomJoueur.getPosition();
    let newIndice = calcIndicePion(newPos, classNomJoueur.getNumero());
    let isCarte = false;
    if(newPos === 40){
        document.getElementById('alertPrison').style.display = 'block';
        desactiverBoutons();
        //le joueur va en prison
        rejoue = false;
        dicoJoueurs[nomJoueur].reinitCompteurPrison();  //on réinitialise le compteur de double => déjà en prison
    }
    else if(newPos === 0){
        dicoJoueurs[nomJoueur].addSolde(200);
        newSoldeBancaire(nomJoueur)
    }
    else{
        isCarte = afficheCarte(newPos);
    }

    let pion = document.getElementById(nomJoueur);
    pion.style.top = newIndice[0] + "%";
    pion.style.left = newIndice[1] + "%";
    dicoJoueurs[nomJoueur].setPosition(newPos);

    return isCarte;
}