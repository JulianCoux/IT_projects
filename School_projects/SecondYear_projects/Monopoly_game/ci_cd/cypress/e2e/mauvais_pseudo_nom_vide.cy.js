// Test d'entrée d'un mauvais pseudo : ne rien mettre

describe('template spec', () => {
    it('passes', () => {
      cy.visit('http://localhost:8000')
      // On se déplace sur la page partie locale
      cy.get('.container a:nth-child(2)').click()
  
      const stub = cy.stub()          // On crée un eventListener
      cy.on ('window:alert', stub)
      cy.get('.btn-plus').click()
      cy.then(() => {                 // On attend que l'event soit trigger        
        expect(stub.getCall(0)).to.be.calledWith('Veuillez entrer un nom de joueur.')
      })
    })
  })