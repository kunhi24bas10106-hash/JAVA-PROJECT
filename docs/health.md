# Health

Health determines the maximum amount of damage any entity can take, for instance, before being destroyed.
The health of an entity is measured in discrete steps, often called hit points (HP).

The `HealthAuthoritySystem` handles changes to an entity's maximum health.
The `HealthClientSystem` manages the current health of entities with health as well as the UI elements visually representing an entity's health status.

Access the `HealthComponent` to retrieve information about an entity's maximum and current health, its damage thresholds, and whether or not the entity should be destroyed if its health drops to 0.
