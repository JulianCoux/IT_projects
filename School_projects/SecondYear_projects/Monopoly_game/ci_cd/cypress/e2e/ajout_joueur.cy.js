// Test d'ajout de joueur dans la partie locale
// **Scénario** :
// Ajouter le joueur "Alice"
// Ajouter le joueur "Bob"

describe('template spec', () => {
  it('passes', () => {
    cy.visit('http://localhost:8000')
    // On se déplace sur la page partie locale
    cy.get('.container a:nth-child(2)').click()

    // On ajoute le joueur 'Alice' et on vérifie que c'est bon
    cy.get('#username').type('Alice')
    cy.get('.btn-plus').click()
    cy.get('#joueursContainer .numero').contains('Joueur 1')
    cy.get('#joueursContainer .nomJoueur').contains('Alice')

    // On ajoute le joueur 'Bob' et on vérifie que c'est bon
    cy.get('#username').type('Bob')
    cy.get('.btn-plus').click()
    cy.get('#joueursContainer .joueur:nth-child(2) .numero').contains('Joueur 2')
    cy.get('#joueursContainer .joueur:nth-child(2) .nomJoueur').contains('Bob')

    // On ajoute 4 autres joueurs
    cy.get('#username').type('Celine')
    cy.get('.btn-plus').click()
    cy.get('#username').type('Dorian')
    cy.get('.btn-plus').click()
    cy.get('#username').type('Etienne')
    cy.get('.btn-plus').click()
    cy.get('#username').type('Francois')
    cy.get('.btn-plus').click()
    // On vérifie qu'on a bien 6 joueurs
    cy.get('#joueursContainer > div').should('have.lengthOf', 6)

    
    // On essaye d'ajouter un joueur supplémentaire
    const stub = cy.stub()          // On crée un eventListener
    cy.on ('window:alert', stub)
    cy.get('#username').type('Gerard')
    cy.get('.btn-plus').click()
    cy.then(() => {                 // On attend que l'event soit trigger        
      expect(stub.getCall(0)).to.be.calledWith('Vous avez atteint le nombre maximum de joueurs (6).')
    })

    // On va retirer 3 joueurs : A, D, et B
    cy.get('#joueursContainer .joueur:nth-child(1) .btn-annule').click()
    cy.get('#joueursContainer .joueur:nth-child(3) .btn-annule').click()
    cy.get('#joueursContainer .joueur:nth-child(1) .btn-annule').click()
    cy.get('#joueursContainer > div').should('have.lengthOf', 3)

    // On va mtn tester d'ajouter des noms invalides
    // Nom avec des caractères spéciaux
    // const stub2 = cy.stub()          // On crée un eventListener
    // cy.on ('window:alert', stub2)
    // cy.get('#username').type('Eçadsq')
    // cy.get('.btn-plus').click()
    // cy.then(() => {                 // On attend que l'event soit trigger        
    //   expect(stub2.getCall(0)).to.be.calledWith('Le nom de joueur ne doit contenir que des lettres et des chiffres.')
    // })

    // Nom trop long

    // Nom vide
  })
})