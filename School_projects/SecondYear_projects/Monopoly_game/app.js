import { createServer } from 'node:http';
import { Server } from 'socket.io';
import { app } from './routes/app.js';

const server = createServer(app);
export const io = new Server(server);
const port = 8000;

/* le serveur écoute sur le port donné (les requêtes se font sur ce port) */
server.listen(port, () => {
    console.log(`server running at http://localhost:${port}/`);
});

const rooms = []; // tableau contenant toutes les 'rooms' (correspond à une partie)

/* L'évènement 'connection' est un évènement déclenché dès qu'un client se connecte au serveur */
io.on('connection', (socket) => {
    console.info(`[connection] ${socket.id}`);
    let room = null;
    let roomLength = null;

    /* lorqu'on reçoit l'événement 'numberOfPlayer', on assigne la valeur donnée à notre variable */
    socket.on("numberOfPlayer", (nbPlayer) =>{
        console.log(`[numberOfPlayer] ${nbPlayer}`)
        roomLength = nbPlayer;
    })

    /* Lorsqu'on reçoit l'évènement 'playerData' :
    * 1. Soit le joueur est host, il n'a donc pas de room associé.
    *   On lui crée une room, et on lui envoie son identifiant de room
    * 2. Soit c'est un joueur qui veut rejoindre une room grâce à un identifiant.
    *   On cherche la room correspondante, si elle existe, on ajoute le joueur
    *   dans les joueurs de la room et on lui envoie les joueurs qui sont déjà dans la room.
    * Enfin, on envoie au autre joueur déjà présent dans la room qu'un nouveau est arrivé et
    * on ajoute le nouveau dans la room.
    */
    socket.on("playerData", (joueur) => {
        console.info(`[playerData] ${joueur.username}`)

        if (!joueur.roomID){
            room = createRoom(joueur, roomLength);
            console.info(`[createRoom] - ${room.id} - ${joueur.username}`);
            io.to(socket.id).emit('your room', room.id);
        } else {
            room = rooms.find(r => r.id === joueur.roomID);

            if (room === undefined){
                return;
            } else if (room.full){
                io.to(socket.id).emit('room full');
                return;
            }
            console.info(`[joinRoom] - ${room.id} - ${joueur.username}`);
            room.joueurs.push(joueur);
            io.to(socket.id).emit('waiting', room.joueurs);
        }

        io.to(room.id).emit('join room', joueur);
        socket.join(room.id);

        console.debug(`[roomLength] ${room.joueurs.length}`);
        console.debug(`[expectedRoomLength] ${room.expectedLength}`);
        const start = room.joueurs.length === parseInt(room.expectedLength);
        console.debug(`[startGame] is the game going to start ? ${start}`)
        if (start){
            console.debug(`[startGame] this is the beginning of the game`);
            room.full = true;
            io.to(room.id).emit('go game');
        }
    })

    /* Le serveur est averti que les clients ont bien été redirigé.
    * Les clients ont un nouvel socket.id donc le serveur les ajoute dans la room donnée en paramètre
    * Si la room existe, le serveur prévient que le jeu va commencer à ce client */
    socket.on('in game', (roomID) => {
        socket.join(roomID)
        console.log(`[joinRoom] - ${socket.id} - ${roomID}`)
        const room = rooms.find(r => r.id === roomID)
        if (room !== undefined ){
            console.debug(room.turnOf)
            io.to(socket.id).emit('start game', room.joueurs, room.joueurs[room.turnOf].username, room.turnOf)
        } else {
            console.log('[Erreur] - room pas définie')
        }
    })

    socket.on('finishTurn', (roomID, joueurs) => {
        console.log('next turn')
        const room = rooms.find(r => r.id === roomID)
        if (room !== undefined ){
            io.to(room.id).emit('nextTurn', room.joueurs[(++room.turnOf)%room.expectedLength].username, joueurs)
        } else {
            console.log('[Erreur] - room pas définie')
        }
    })

    /* Evènement produit lorsque quelqu'un se déconnecte du serveur */
    socket.on("disconnect", () => {
        console.info(`[disconnect] ${socket.id}`);
    })

    /* L'évènement 'host quit' est reçu.
    * On supprime la room du tableau pour pas que de nouveau joueur puisse y accéder.
    * On avertit ceux qui y étaient déjà, pour qu'ils partent */
    socket.on("host quit", (joueur) => {
        console.info(`[host][disconnect] ${joueur.username} - ${socket.id}`);
        let i = 0;
        while (i < rooms.length && rooms[i].id !== joueur.roomID){
            i+=1;
        }
        if (i < rooms.length){
            rooms.splice(i, 1);
        }
        console.info(`[RoomDeleted] ${joueur.roomID}`);
        io.to(joueur.roomID).emit("kick");
    })

    /* Evènement reçu lorsqu'un joueur quitte une room.
    * On supprime ce joueur du tableau contenu dans l'objet room.
    * Ensuite, on prévient les autres joueurs de cette room que le joueur a quitté. */
    socket.on("player quit", (joueur) => {
        console.info(`[player][disconnect] ${joueur.username} - ${socket.id}`);
        let room = rooms.find(r => r.id === joueur.roomID);
        let i = 0;
        console.log('joueurs de la room', room.joueurs)
        while (i < room.joueurs.length && room.joueurs[i].username !== joueur.username){
            console.debug(`[PLayerUsername] ${room.joueurs[i].username}`);
            i+=1;
        }
        if (i < room.joueurs.length){
            console.debug(`[IndexPlayerWhoQuit] ${i}`);
            room.joueurs.splice(i, 1);
            if (room.full) room.full = false;
        }
        console.debug(`[NewRoomLength] ${room.joueurs.length}`);
        console.info(`[PlayerQuit] ${joueur.username}`);
        io.to(joueur.roomID).emit("quit room", room.joueurs);
    })

    socket.on('finishMoving', (roomID, joueur) => {
        io.to(roomID).emit("updateMoving", joueur);
    })

    socket.on('showSales', (roomID, joueur, joueurSuivant, valeur, couleur) => {
        io.to(roomID).emit("afficheEnchere", joueur, joueurSuivant, valeur, couleur);
    })

    socket.on('endEnchere', (roomID, joueurs) => {
        io.to(roomID).emit("suppEnchere", joueurs);
    })

    socket.on('new ranking', (roomID, joueurs) => {
        io.to(roomID).emit('update ranking', joueurs);
    })

    socket.on('draw card', (roomID, cardType) => {
        socket.to(roomID).emit(cardType)
    })

});

/* Permet de créer une room.
* L'objet contient :
*   1. L'identifiant de la room
*   2. Un tableau des joueurs qui sont dedans
*   3. La taille de la room décidé par le host
*/
function createRoom(joueur, roomLength){
    const room = {id: roomID(), joueurs: [], expectedLength: roomLength, full: false, turnOf:  getRandomInt(roomLength-1)};

    joueur.roomID = room.id;
    room.joueurs.push(joueur);
    rooms.push(room);

    return room;
}

/* Génère un identifiant de room de manière aléatoire */
function roomID(){
    return Math.random().toString(36).substring(2, 9);
}

function getRandomInt(max){
    return Math.floor(Math.random() * max)
}
