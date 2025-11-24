# Damage

Damage refers to the reduction of health points caused by the affected entity itself or a third party.
Self-inflicted causes include, for instance, falling, drowning, or poison damage.
Damage inflicted by a third party typically involves some form of hitting the affected entity.

* The `DamageSystem` handles damage dealt to entities with health.
* The `BlockDamageAuthoritySystem` enables blocks to sustain some damage before getting destroyed.
* The `DamageEffectAuthoritySystem` produces block particle effect in the event of damage.

Send a `DoDamageEvent` to a specific entity to deal damage to it.

Use the `DamageResistComponent` to configure an entity to not take damage from specific damage types.
Examples for damage types are drowning (`engine:drowningDamage`) or explosive (`engine:explosiveDamage`) damage.
The `DamageAuthoritySystem` considers the settings provided by this component before an entity is damaged.

Damage sounds added to an entity via the `DamageSoundComponent` are randomly selected and played when the entity is damaged.
Sounds should be referenced as `[engine|<module>]:<soundFileName>` for sound files located in the `assets/sounds` directory of the engine or a module, for instance `engine:Slime3`.

Event chain:
* `DoDamageEvent`
* `BeforeDamageEvent`
* _Entity damaged, health component saved_
* `OnDamagedEvent`

Commands:
* `damageResist(damagetype,percentage)`: gives resistance to damage (damagetype = all for total resistance).
* `damageImmune(damagetype)`: percentage = 100 by default.
* `checkResistance()`: gives list of active resistance values
