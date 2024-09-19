// Test de joueur en prison
// **Scénario** :
// On veut faire aller Alice en prison à de multiples
// reprises.
// - elle sort 1er tour en payant
// - elle ne fait jamais de double et est obligée de payer
// - elle fait un double au 2eme tour
// - elle paye au deuxieme tour
// - elle recoit bien les sous quand autre joueur tombe
//   sur une de ses propriétés

function coupModeTest() {
    cy.get('@champ').clear()
    cy.get('@champ').type("40;0")
    cy.get('@submit').click()
    cy.get('@finTour').click()
}

function lancerDes(value) {
    cy.get('@champ').type(value)
    cy.get('@submit').click()
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
    } else if (actionJoueur == "entreePrison") {
        // TODO
    } else if (actionJoueur == "sortirPrison") {
        // TODO
    } else if (actionJoueur == "enchere") {
        cy.get('.enchere').click()
        cy.get('#pseudo').select(1)
        cy.get('#price').type(0)
        cy.contains("VALIDER").click()
    } else if (actionJoueur == "parcGratuit") {
        // TODO
    }
}

describe('Test de la prison', () => {
    it('works for the jail', () => {
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

        // Alice va en prison
        lancerDes(30)
        cy.wait(10000).get('.alert-prison-button').click()
        // Fin du tour
        cy.get('@finTour').click()

        // modeTest joue son tour
        coupModeTest()

        // Alice paye direct
        cy.get('#btnSortirPrison').click()
        // Verif qu'elle est toujours case 10
        cy.get('#position').should('have.text', " " + 10)
        // Elle lance les dés, fait 1, achete la maison (elle est donc sortie
        // de prison, besoin de rien assert)
        lancerDes(1)
        action("acheter")
        // Fin du tour
        cy.get('@finTour').click()

        // Alice retourne en prison
        lancerDes(19)
        cy.wait(10000).get('.alert-prison-button').click()
        // Fin du tour
        cy.get('@finTour').click()

        

        // On continue ....
        // TODO

  
      
    })
  })