const joueur = {
    host : false,
    roomID : null,
    username : "",
    socketID : "",
    turn : false,
    win : false
};

const socket = io();

function main(){
    const usernameForm = document.getElementById("pseudo-form");
    const usernameInput = document.getElementById("pseudo-input");
    const rejoindreForm = document.getElementById("rejoindre-form");
    const rejoindreDiv = document.getElementById("rejoindre-div");
    const codeInput = document.getElementById("code");
    const usernameDiv = document.getElementById("pseudo-div");
    const waitingArea = document.getElementById("waiting-area");
    const enAttente = document.getElementById("en-attente");
    const modelWaiter = document.getElementById("model-waiter");

    /* Ajoute un joueur dans le fichier html, dans la 'zone d'attente' */
    function addPlayerInWaitingArea(joueur){
        let liWaiter = document.createElement("li");
        let waiter = modelWaiter.cloneNode(true);
        waiter.classList.remove("d-none");
        waiter.querySelector("div").textContent = joueur.username;
        liWaiter.appendChild(waiter);
        waitingArea.querySelector("ul").appendChild(liWaiter);
    }


    /* Récupère le nom de joueur dans l'input
    * Ensuite, on remplie l'objet JSON joueur */
    usernameForm.addEventListener("submit", (e) => {
        e.preventDefault();
        joueur.username = usernameInput.value;
        joueur.host = false;
        joueur.turn = false;
        joueur.socketID = socket.id;
        rejoindreDiv.classList.remove("d-none"); //affiche l'élément
        usernameInput.disabled = true; // Le joueur ne peut plus changer son pseudo
    })

    /* Récupère le code de la room que le joueur veut rejoindre.
    * S'il y a un code, on l'ajoute à l'objet JSON puis on envoie cet objet au serveur */
    rejoindreForm.addEventListener("submit", function (e){
        e.preventDefault();
        if (codeInput.value !== ""){
            joueur.roomID = codeInput.value;
            socket.emit("playerData", joueur);
        } else {
            alert("Vous devez entrer le code d'un salon pour le rejoindre");
        }
    })

    /* Lorsqu'un joueur fait retour, on dit au serveur qui a quitté
    * Ce qui est bien avec cette évenenement, c'est qu'on peut récupérer les infos du joueur comme son id de room */
    document.querySelector("a.btn-retour").addEventListener("click", function () {
        if (roomID){
            console.info(`[PlayerQuit] ${joueur.username} quit his room`);
            socket.emit("player quit", joueur);
        }
    })

    /* Ajoute les joueurs qui sont déjà dans la room quand le joueur rejoint, dans le fichier html du joueur. */
    socket.on("waiting", (joueurs) => {
        usernameDiv.classList.add("d-none");
        rejoindreDiv.classList.add("d-none");
        waitingArea.classList.remove("d-none");
        enAttente.classList.remove("d-none");
        joueurs.forEach(addPlayerInWaitingArea);
    })

    socket.on('room full', () => {
        alert("Ce salon est déjà plein");
    })

    /* Ajoute le joueur qui vient de rejoindre la room au fichier html */
    socket.on("join room", addPlayerInWaitingArea);

    /* Evenement reçu lorsque l'hôte quitte notre room */
    socket.on("kick", () => {
        console.log(`[Kick] ${joueur.username} has been kicked`);
        document.location.href = "../index.html";
    })

    /* Evenement reçu lorsqu'un joueur quitte notre room */
    socket.on("quit room", (joueurs) => {
        waitingArea.querySelector("ul").textContent = ""; // on enlève tous les joueurs affichés
        joueurs.forEach(addPlayerInWaitingArea); // on les re-ajoute sans celui qui est parti
    })

    /* Lorsque tous les joueurs sont là, le serveur nous prévient (évènement 'start game').
    *  On est redirigé sur la page du plateau de jeu.
    */
    socket.on("go game", () => {
        console.info(`[Redirection] ${joueur.username} is redirected`);
        document.location.replace(`../pages/plateau.html?roomID=${joueur.roomID}&mode=online&username=${joueur.username}`);
    })
}

window.addEventListener('load', main, false);