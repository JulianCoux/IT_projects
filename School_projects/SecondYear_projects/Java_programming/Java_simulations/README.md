## Game simulation in Java : 

# Programming in Java of several graphical interfaces to model the simulation of several games:

-Conway games: modelling of an array of boxes, which change state depending on the state of neighbouring boxes. Graphical display using GUISimulator
-Game of life: Like Conway games but with a number N of states.
-Segregation game: Simulation of Schelling's Segregation Model
-Implementation of Boids: the objective was to simulate the behaviour of birds when they fly in a pack. Each bird is attracted to members of its own species to form a group. In the implementation, several forces are applied to these Boids to simulate their behaviour: a force to make them attract each other, a force to make them repel each other if they are going to collide and a force that pushes them away from the edge of the display screen. We also created an extension to model the behaviour of sharks in the middle of a group of sardines: the sardines are attracted to each other but flee from the sharks. Whereas the sharks don't care about the other sharks but are drawn more quickly to the Sardines to chase them away.
Use of inheritance to factor the programme between different simulations. Use of Collections to improve list traversal performance.
