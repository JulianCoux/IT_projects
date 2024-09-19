// Bug qui permet à un joueur d'avoir un solde négatif sans
// que la partie se termine
// **Scénario** :
// Joueur1 avance de 5 et met la gare aux enchères
// Joueur2 achète la gare 1500
// Joueur 2 avance de 4

// import {random, floor} from Math;

function lancerDes(coup) {
    cy.get('@champ').type(coup)
    cy.get('@submit').click()
    cy.get('@champ').clear()
}

function action(actionJoueur) {
    if (actionJoueur == "acheter") {
        cy.get('.acheter').click()
    } else if (actionJoueur == "payerLoyer") {
        cy.get('.payer').click()
    } else if (actionJoueur == "payerLuxe") {
        cy.get('.alert-taxeLuxe-button').click()
    } else if (actionJoueur == "payerImpot") {
        cy.get('.alert-impots-button').click()
    } else if (actionJoueur == "enchere") {
        cy.get('.enchere').click()
        cy.get('#pseudo').select(1)
        cy.get('#price').type(1500)
        cy.contains("VALIDER").click()
    }
}


describe('Test de partie longue', () => {
    it('works for a long game', () => {

        // ###############################################################
        //              Lancement d'une partie locale
        // ###############################################################
        cy.visit('http://localhost:8000')
        // On se déplace sur la page partie locale
        cy.get('.container a:nth-child(2)').click()

        // On ajoute le joueur 'Alice' 
        cy.get('#username').type('Alice')
        cy.get('.btn-plus').click()

        // On ajoute le joueur 'modeTest' pour activer le mode de test
        cy.get('#username').type('modeTest')
        cy.get('.btn-plus').click()

        // On lance la partie
        cy.get('#lienJouer').click()


        // ###############################################################
        //         Récupérationdes éléments récurrents du DOM
        // ###############################################################

        // On recupere le champ d'input et le submit
        cy.get('#testInput').as('champ')
        cy.get('#testButton').as("submit")

        // Le bouton fin de tour
        cy.get('#buttonSuivant').as("finTour")


        // ###############################################################
        //                   On démarre la partie 
        // ###############################################################

        // Alice fait 5
        lancerDes(5);
        // Mise aux enchères et modeTest l'achète 1500
        action("enchere")
        cy.get('@finTour').click()

        // modeTest fait 4
        lancerDes(4)
        cy.get(".alert-impots-button").click();

        // On vérifie qu'on a perdu
        cy.get('.alert-gagne-button').click()
    })
  })