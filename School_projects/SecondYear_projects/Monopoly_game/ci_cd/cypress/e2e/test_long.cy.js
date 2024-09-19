// On joue une partie avec une centaine de coups
// **Scénario** :
// C'est une partie à deux joueurs.
// Les coups des joueurs et leur comportemen sont définis à l'avance
// A chaque tour, on vérifie que la position du joueur est la bonne 
// et son compte en banque aussi.
// Par soucis de simplicité, nous évitons les cartes chances et caisses
// de communauté qui apportent de l'aléatoire.

// import {random, floor} from Math;

function lancerDes(desJoueur, coup) {
    cy.get('@champ').type(desJoueur[coup])
    cy.get('@submit').click()
    cy.get('@champ').clear()
}

function action(actionJoueur, coup) {
    if (actionJoueur[coup] == "acheter") {
        cy.get('.acheter').click()
    } else if (actionJoueur[coup] == "payerLoyer") {
        cy.get('.payer').click()
    } else if (actionJoueur[coup] == "payerLuxe") {
        cy.get('.alert-taxeLuxe-button').click()
    } else if (actionJoueur[coup] == "payerImpot") {
        cy.get('.alert-impots-button').click()
    } else if (actionJoueur[coup] == "entreePrison") {
        // TODO
    } else if (actionJoueur[coup] == "sortirPrison") {
        // TODO
    } else if (actionJoueur[coup] == "enchere") {
        cy.get('.enchere').click()
        cy.get('#pseudo').select(1)
        cy.get('#price').type(0)
        cy.contains("VALIDER").click()
    } else if (actionJoueur[coup] == "parcGratuit") {
        // TODO
    }
}

function verif(argentJoueur, position, coup) {
    // Verif de la position du joueur
    cy.get('#position').should('have.text', " " + position)

    // Verif du compte en banque
    cy.get('#solde_compte').should('have.text', argentJoueur[coup])

    // /!\ prendre en compte le chgt quand on paye
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
        //               Initialisation des tableaux
        // ###############################################################

        // Pour Alice
        let positionAlice = 0
        const desAlice = [3, 8, 7, 10, 3, 8, 4, 7, 4, 9, 6, 5, 3, 8, 6, 5]
        const actionAlice = ["acheter", "acheter", "acheter", "acheter", "enchere", "acheter", "payerLoyer", "simpleVisite", "acheter", "payerLoyer", 
        "payerLoyer", "enchere", "acheter", "payerLoyer", "payerLoyer", "acheter"]
        const argentAlice = [1440, 1300, 1120, 980, 980, 580, 780, 780, 620, 602, 578, 578, 228, 378, 378, 210]

        // Pour modeTest
        let positionModeTest = 0
        const desModeTest = [5, 4, 2, 4, 8, 6, 9, 10, 5, 2, 6, 10, 10, 4, 9, 7] // 4, 7, 9 --> l=16
        const actionModeTest = ["acheter", "acheter", "payerLoyer", "acheter", "acheter", "acheter", "payerLuxe", "acheter", "acheter", "payerLoyer", 
        "enchere", "payerLoyer", "acheter", "payerLoyer", "payerLoyer", "payerLoyer"]
        const argentModeTest = [1300, 1180, 1170, 970, 750, 470, 370, 470, 330, 348, 372, 372, 512, 562, 550, 532]


        // ###############################################################
        //                   On démarre la partie 
        // ###############################################################

        for (let i=0; i<desAlice.length; i++) {
            // On joue et fait les verifs de Alice
            lancerDes(desAlice, i)
            positionAlice = (positionAlice + desAlice[i]) % 40
            action(actionAlice, i)
            verif(argentAlice, positionAlice, i)
            cy.get('@finTour').click()

            // On joue et fait le tour de modeTest
            lancerDes(desModeTest, i)
            positionModeTest = (positionModeTest + desModeTest[i]) % 40
            action(actionModeTest, i)
            verif(argentModeTest, positionModeTest, i)
            cy.get('@finTour').click()
        }  
    })
  })