# Flocking and avoidance with boids
Project for Sub-symbolic AI methods on NTNU (Norwegian University of Science and Technology)
The project is an implementation of boids in Java.

A flock (or a school or a swarm) is a big group of individual organisms moving together. The
behavior they exhibit is called flocking. In general, there is no group leader. There is also no
global information, for instance about where they are heading. Each individual is just following
what the closest other individuals are doing, so they behave only according to local information.
In 1986, Reynolds came up with a mathematical model for this flocking behavior. The model
is just a set of simple rules that each individual follows. Reynolds gave objects following these
rules the name boids, from "bird-oid object" or bird-like object. The three basic rules for a boid
are:
* Separation, a boid will steer to avoid crashing with other boids close to it.
* Alignment, a boid will steer towards the average heading of other boids close to it.
* Cohesion, a boid will steer towards the average position of other boids close to it.