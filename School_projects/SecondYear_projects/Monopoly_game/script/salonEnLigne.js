/* Objet JSON représentant un joueur.
* Il faudra le remplacer par une class javascript */
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
    const usernameInput = document.getElementById("username");
    const nombreJoueur = document.getElementById('nombre-joueurs-div');
    const userCard = document.getElementById("usercard-div");
    const waitingArea = document.getElementById("waiting-area");
    const codeArea = document.getElementById("code-area");
    const playersContainer = document.getElementById("players-container");
    const modelWaiter = document.getElementById("model-waiter");

    /* Ajoute un joueur dans le fichier html, dans la 'zone d'attente' */
    function addPlayerInWaitingArea(joueur){
        let liWaiter = document.createElement("li");
        let waiter = modelWaiter.cloneNode(true);
        waiter.classList.remove("d-none");
        waiter.querySelector("div").textContent = joueur.username;
        liWaiter.appendChild(waiter);
        playersContainer.appendChild(liWaiter);
    }

    /* Récupère le nombre de joueurs que le host a choisi grâce aux input-radio
       et envoie l'évenement 'numberOfPlayer' au serveur */
    document.getElementsByName("nombreJoueur").forEach((radio) => {
        radio.addEventListener("click", function (e){
            e.preventDefault();

            nombreJoueur.classList.add("d-none");
            userCard.classList.remove("d-none");

            socket.emit('numberOfPlayer', radio.value);
        })
    })

    /* Récupère le nom de joueur de l'hôte puis on envoie les infos du joueur (objet JSON) au serveur.
    * Ensuite, le serveur nous envoie notre identifiant de room qu'on affiche pour que le joueur puisse le partager */
    document.getElementById("form").addEventListener("submit", function (e){
        e.preventDefault();
        joueur.username = usernameInput.value;
        joueur.host = true;
        joueur.turn = true;
        joueur.socketID = socket.id;

        userCard.classList.add("d-none");
        waitingArea.classList.remove("d-none");
        codeArea.classList.remove("d-none");

        socket.emit('playerData', joueur);
        socket.on('your room', (roomID) => {
            codeArea.textContent += roomID;
            joueur.roomID = roomID;
            addPlayerInWaitingArea(joueur);
        })
    })

    /* Prévient le serveur si l'hôte quitte la room */
    document.querySelector("a.btn-retour").addEventListener("click", function () {
        console.info(`[HostQuit] ${joueur.username} quit his room`);
        socket.emit("host quit", joueur);
    })

    /* Lorqu'un joueur rejoint la room, on l'ajoute sur la page html de l'hôte */
    socket.on("join room", addPlayerInWaitingArea);

    /* Evenement reçu lorsqu'un joueur quitte notre room */
    socket.on("quit room", (joueurs) => {
        if (roomID){
            playersContainer.textContent = ""; // on enlève tous les joueurs affichés
            joueurs.forEach(addPlayerInWaitingArea); // on les re-ajoute sans celui qui est parti
        }
    })

    /* Lorsque tous les joueurs sont là, le serveur nous prévient (évènement 'go game').
    * On est redirigé sur la page du plateau de jeu.
    */
    socket.on("go game", () => {
        console.log(`[Redirection] ${joueur.username} is redirected`);
        document.location.replace(`../pages/plateau.html?roomID=${joueur.roomID}&mode=online&username=${joueur.username}`);
    })

}

window.addEventListener('load', main, false);