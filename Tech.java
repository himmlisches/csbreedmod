import java.io.*;
import javax.swing.*;

public class Tech implements Serializable
{
    private static final long serialVersionUID = 4L;
    int prereqsRequired;
    int cost;
    String name;
    String description;
    String tooltip;
    Tech[] prereqs;
    Boolean owned;
    
    public void assignTooltip(final int index, final WorldState w) {
        if (index == 0) {
            this.tooltip = "View Chosen profiles";
        }
        else if (index == 1) {
            this.tooltip = "Evac req 100 -> 120";
        }
        else if (index == 2) {
            this.tooltip = "Exterm req 100 -> 140";
        }
        else if (index == 3) {
            this.tooltip = "Can make Commanders for 1 EE";
            if (w.save != null && w.save.harem != null && w.save.harem.length > 0) {
                this.tooltip = String.valueOf(this.tooltip) + " and deploy Forsaken";
            }
        }
        else if (index == 4) {
            this.tooltip = "Evac req 120 -> 140";
        }
        else if (index == 5) {
            this.tooltip = "Exterm req 140 -> 200";
        }
        else if (index == 6) {
            this.tooltip = "+50% circumstance damage when surrounded";
        }
        else if (index == 7) {
            this.tooltip = "+20% trauma with the four basic attacks";
        }
        else if (index == 8) {
            this.tooltip = "+1 Commander duration for +1 EE";
        }
        else if (index == 9) {
            this.tooltip = "Commanders can wait to capture";
        }
        else if (index == 10) {
            this.tooltip = "Commander inflicts HATE";
        }
        else if (index == 11) {
            this.tooltip = "Commander inflicts PLEA";
        }
        else if (index == 12) {
            if (w.tickle()) {
                this.tooltip = "Commander inflicts ANTI";
            }
            else {
                this.tooltip = "Commander inflicts INJU";
            }
        }
        else if (index == 13) {
            this.tooltip = "Commander inflicts EXPO";
        }
        else if (index == 14) {
            this.tooltip = "+1 Commander duration for +1 EE";
        }
        else if (index == 15) {
            this.tooltip = "+1 Commander capture for +2 EE";
        }
        else if (index == 16) {
            this.tooltip = "Evac req 140 -> 160";
        }
        else if (index == 17) {
            this.tooltip = "Exterm req 200 -> 300";
        }
        else if (index == 18) {
            this.tooltip = "Battle end delayed by 1 turn";
        }
        else if (index == 19) {
            this.tooltip = "Can retreat to gain +1 EE per 5 surround turns";
        }
        else if (index == 20) {
            this.tooltip = "+1 Commander duration for +2 EE";
        }
        else if (index == 21) {
            this.tooltip = "Commander equips two Suppressors for +5 EE";
        }
        else if (index == 22) {
            this.tooltip = "Inseminate-like Commander for +6 EE";
        }
        else if (index == 23) {
            this.tooltip = "Force Orgasm-like Commander for +6 EE";
        }
        else if (index == 24) {
            if (w.tickle()) {
                this.tooltip = "Force Laughter-like Commander for +6 EE";
            }
            else if (w.getGenderBalance()[2] == 0) {
                this.tooltip = "Sodomize-like Commander for +6 EE";
            }
            else if (w.getGenderBalance()[1] == 0 && w.getGenderBalance()[3] == 0) {
                this.tooltip = "Torture-like Commander for +6 EE";
            }
            else {
                this.tooltip = "Sodomize/Torture-like Commander for +6 EE";
            }
        }
        else if (index == 25) {
            this.tooltip = "Broadcast-like Commander for +6 EE";
        }
        else if (index == 26) {
            this.tooltip = "+1 Commander duration for +2 EE";
        }
        else if (index == 27) {
            this.tooltip = "+1 Commander capture for +5 EE";
        }
        else if (index == 28) {
            this.tooltip = "Evac req 160 -> 200";
        }
        else if (index == 29) {
            this.tooltip = "Exterm req 300 -> 500";
        }
        else if (index == 30) {
            this.tooltip = "Spend turns to increase damage for rest of battle";
        }
        else if (index == 31) {
            this.tooltip = "Act immediately after surrounding target";
        }
        else if (index == 32) {
            this.tooltip = "+1 Commander capture for +10 EE";
        }
        else if (index == 33) {
            this.tooltip = "Commander equips Suppressor and Defiler for +10 EE";
        }
        else if (index == 34) {
            this.tooltip = "Commander inflicts HATE/FEAR, Total Morality Break against Slaughter users";
        }
        else if (index == 35) {
            this.tooltip = "Commander inflicts PLEA/DISG, Total Innocence Break against Fantasize users";
        }
        else if (index == 36) {
            if (w.tickle()) {
                this.tooltip = "Commander inflicts ANTI/TICK, Total Confidence Break against Detonate users";
            }
            else {
                this.tooltip = "Commander inflicts INJU/PAIN, Total Confidence Break against Detonate users";
            }
        }
        else if (index == 37) {
            this.tooltip = "Commander inflicts EXPO/SHAM, Total Dignity Break against Striptease users";
        }
        else if (index == 38) {
            this.tooltip = "+1 Commander capture for +20 EE";
        }
        else if (index == 39) {
            this.tooltip = "Commander becomes capable of capturing flying Chosen for +10 EE";
        }
        else if (index == 40) {
            this.tooltip = "During final battle, Unbreakable Friends wait extra turn before killing each other";
        }
        else if (index == 41) {
            this.tooltip = "Before final battle, Chosen lose 10% Resolve per Rival, 40% Resolve per bitter Enemy";
        }
        else if (index == 42) {
            this.tooltip = "Threaten deals double damage.  During final battle, removes 3% x FEAR level Resolve per Impregnated ally";
        }
        else if (index == 43) {
            this.tooltip = "Slime deals double damage.  During final battle, removes 4% x DISG level Resolve if Hypnotized";
        }
        else if (index == 44) {
            if (w.tickle()) {
                this.tooltip = "Poke deals double damage.  During final battle, removes 3% x TICK level Resolve if Drained or 1% x TICK level if not";
            }
            else {
                this.tooltip = "Attack deals double damage.  During final battle, removes 3% x PAIN level Resolve if Drained or 1% x PAIN level if not";
            }
        }
        else if (index == 45) {
            this.tooltip = "Taunt deals double damage.  During final battle, removes 5% x SHAM level Resolve if Parasitized and surroundable";
        }
        else if (index == 46) {
            this.tooltip = "+2 Commander duration for +30 EE";
        }
        else if (index == 47) {
            this.tooltip = "Commander equips Suppressor and Defiler and Punisher for +60 EE";
        }
        else if (index == 48) {
            this.tooltip = "Immediately begins the final battle";
        }
    }
    
    public String getTooltip() {
        return this.tooltip;
    }
    
    public int getPrereqReqs() {
        return this.prereqsRequired;
    }
    
    public void buy(final WorldState w) {
        this.owned = true;
        w.addEnergy(0 - this.cost);
    }
    
    public int getCost() {
        return this.cost;
    }
    
    public void initialize(final int index, final WorldState w) {
        if (index == 0) {
            this.name = "Psychic Reading";
            this.prereqsRequired = 0;
            this.cost = 1;
            this.prereqs = new Tech[0];
            this.description = "Refines your innate control over the psychic energies associated with emotions and the subconscious.  Once this has been purchased, the personal profiles of the Chosen are added to the Info page.  This allows you to review the status of the Chosen you've met before, including their vulnerabilities and their progress toward corruption.  The latter information is also displayed when you 'Examine' targets in battle.";
        }
        else if (index == 1) {
            this.name = "Wide Deployment";
            this.prereqsRequired = 0;
            this.cost = 1;
            this.prereqs = new Tech[0];
            this.description = "Develops your ability to command Demons and Thralls in a wider area at once.  This forces the humans to evacuate more people before permitting the Chosen to use their full power.  Increases evacuation requirement from 100 to 120.";
        }
        else if (index == 2) {
            this.name = "Fast Breeders";
            this.prereqsRequired = 0;
            this.cost = 1;
            this.prereqs = new Tech[0];
            this.description = "Develops your captive humans in order to let them give birth to Demons more frequently.  The Chosen will have to slog through more enemies before they can consider the extermination complete.  Increases extermination requirement from 100 to 140.";
        }
        else if (index == 3) {
            this.name = "Focus";
            this.prereqsRequired = 0;
            this.cost = 1;
            this.prereqs = new Tech[0];
            this.description = "Allows your captive humans to give birth to a body you can directly control in order to bring the fight to the Chosen, albeit only at great cost.  Once this has been purchased, options for 'Commander' bodies are unlocked here in the shop.  Every time you send a Commander into battle, it costs Evil Energy.  However, the effects can be quite strong.  The default Commander captures the target Chosen, causing her to start the battle surrounded for 2 turns.  This increases the opening level required to surround her later as normal.";
            if (w.save != null && w.getHarem() != null && w.getHarem().length > 0) {
                this.description = String.valueOf(this.description) + "  This upgrade also allows you to manage the Forsaken brought from previous loops.";
            }
        }
        else if (index == 4) {
            this.name = "Coordinated Deployment";
            this.prereqsRequired = 2;
            this.cost = 2;
            this.prereqs = new Tech[] { w.getTechs()[0], w.getTechs()[1] };
            this.description = "Develops your ability to give your Demons and Thralls detailed strategic orders.  By directing them toward civilian escape routes, you can further slow the evacuation process.  Increases evacuation requirement from 120 to 140.";
        }
        else if (index == 5) {
            this.name = "Eager Breeders";
            this.prereqsRequired = 2;
            this.cost = 2;
            this.prereqs = new Tech[] { w.getTechs()[2], w.getTechs()[3] };
            this.description = "Modifies your captive humans further so that the process of giving birth to Demons causes them to experience mind-shattering ecstasy.  Their cooperation will allow you to grow your army even further.  Increases extermination requirement from 140 to 200.";
        }
        else if (index == 6) {
            this.name = "Weakness Sense";
            this.prereqsRequired = 2;
            this.cost = 2;
            this.prereqs = new Tech[] { w.getTechs()[0], w.getTechs()[2] };
            this.description = "Further develops the psychic link between yourself and your human Thralls.  This allows them to more effectively respond to your instructions to torment surrounded Chosen.  Increases circumstances inflicted against surrounded Chosen by 50%.";
        }
        else if (index == 7) {
            this.name = "Enhanced Polymorphism";
            this.prereqsRequired = 2;
            this.cost = 2;
            this.prereqs = new Tech[] { w.getTechs()[1], w.getTechs()[2] };
            this.description = "Enhances your control over the biology of Demons born to your captive humans.  Your army will be supplied with specialist Demons who have enlarged slime sacs, larger claws, or other similar modifications.  Increases trauma inflicted using the four basic attacks by 20%.";
        }
        else if (index == 8) {
            this.name = "Perception";
            this.prereqsRequired = 2;
            this.cost = 2;
            this.prereqs = new Tech[] { w.getTechs()[0], w.getTechs()[3] };
            this.description = "Allows you to create Commanders capable of using a greater portion of your psychic capacity.  This finer control will make it possible to more effectively subdue captured Chosen.  For +1 Evil Energy per deployment, your Commander's capture duration will increase by 1.";
        }
        else if (index == 9) {
            this.name = "Patience";
            this.prereqsRequired = 2;
            this.cost = 2;
            this.prereqs = new Tech[] { w.getTechs()[1], w.getTechs()[3] };
            this.description = "Develops your ability to control your Commander body.  Instead of launching your ambush immediately at the start of battle, you may save it until later - and it will function at full effectiveness regardless of the target's defense level.  There is no Evil Energy cost to equip your Commander with this ability, but giving the order to capture will consume your action for that round.";
        }
        else if (index == 10) {
            this.name = "Hunger";
            this.prereqsRequired = 2;
            this.cost = 3;
            this.prereqs = new Tech[] { w.getTechs()[1], w.getTechs()[5] };
            this.description = "Strengthens your connection with your Commander body, endowing it with your instinct to gorge yourself on the suffering of the Chosen.  The resulting mouths covering your body are a 'Suppressor'-class upgrade which is not compatible with others of its type.  It can singlehandedly subdue a full-strength Chosen, binding them with countless tongues and gibbering your hateful intensions in the captured Chosen's ear.  There is no Evil Energy cost to apply this upgrade, but its attack is more specialized than that of the basic Commander.";
        }
        else if (index == 11) {
            this.name = "Lust";
            this.prereqsRequired = 2;
            this.cost = 3;
            this.prereqs = new Tech[] { w.getTechs()[2], w.getTechs()[8] };
            this.description = "Strengthens your connection with your Commander body, endowing it with your instinct to fill all minds with sexual excitement.  The resulting tentacles covering your body are a 'Suppressor'-class upgrade which is not compatible with others of its type.  It can singlehandedly subdue a full-strength Chosen, binding them with writhing appendages which seek out their most sensitive places and force pleasure upon them.  There is no Evil Energy cost to apply this upgrade, but its attack is more specialized than that of the basic Commander.";
        }
        else if (index == 12) {
            this.name = "Anger";
            this.prereqsRequired = 2;
            this.cost = 3;
            this.prereqs = new Tech[] { w.getTechs()[0], w.getTechs()[9] };
            this.description = "Strengthens your connection with your Commander body, endowing it with your instinct to cripple all opposition.  The resulting huge muscles covering your body are a 'Suppressor'-class upgrade which is not compatible with others of its type.  It can singlehandedly subdue a full-strength Chosen, beating them down with brute force and dealing grievous injuries even to their supernaturally-durable bodies.  There is no Evil Energy cost to apply this upgrade, but its attack is more specialized than that of the basic Commander.";
        }
        else if (index == 13) {
            this.name = "Mania";
            this.prereqsRequired = 2;
            this.cost = 3;
            this.prereqs = new Tech[] { w.getTechs()[3], w.getTechs()[4] };
            this.description = "Strengthens your connection with your Commander body, endowing it with your instinct to merge with humanity and all its creations.  The resulting devices incorporated into your body are a 'Suppressor'-class upgrade which is not compatible with others of its type.  It can singlehandedly subdue a full-strength Chosen, binding them with chains and attacking them with mundane saws which are too weak to injure them, but which may expose them by shredding their clothes.  There is no Evil Energy cost to apply this upgrade, but its attack is more specialized than that of the basic Commander.";
        }
        else if (index == 14) {
            this.name = "Cunning";
            this.prereqsRequired = 2;
            this.cost = 2;
            this.prereqs = new Tech[] { w.getTechs()[6], w.getTechs()[8] };
            this.description = "Allows you to create Commanders capable of using an even greater portion of your psychic capacity.  This refined control will make it possible to more effectively subdue captured Chosen.  For +1 Evil Energy per deployment, your Commander's capture duration will increase by 1.";
        }
        else if (index == 15) {
            this.name = "Persistence";
            this.prereqsRequired = 2;
            this.cost = 2;
            this.prereqs = new Tech[] { w.getTechs()[7], w.getTechs()[9] };
            this.description = "Develops your ability to concentrate your psychic energy on a single aspect of the human mind.  Your Commander body will be correspondingly more pure and resilient.  For +2 Evil Energy per deployment, your Commander will be able to perform a second capture (provided that the battle does not end beforehand).";
        }
        else if (index == 16) {
            this.name = "Human Collaborators";
            this.prereqsRequired = 2;
            this.cost = 5;
            this.prereqs = new Tech[] { w.getTechs()[4], w.getTechs()[11] };
            this.description = "Develops your ability to make psychic contact with humans outside your hivemind.  Authority figures can be recruited to your side with the promise of Demonic powers or obedient Thralls to satisfy their every sexual fantasy.  In exchange, they will sabotage evacuation efforts.  Increases evacuation requirement from 140 to 160.";
        }
        else if (index == 17) {
            this.name = "Nursery Hives";
            this.prereqsRequired = 2;
            this.cost = 5;
            this.prereqs = new Tech[] { w.getTechs()[5], w.getTechs()[13] };
            this.description = "Modifies your Demons' underground lair to contain biological equivalents of incubators and life support equipment, allowing your captured humans to constantly give birth to Demons with little risk of medical complications. Increases extermination requirement from 200 to 300.";
        }
        else if (index == 18) {
            this.name = "Vengeful Reconstitution";
            this.prereqsRequired = 2;
            this.cost = 5;
            this.prereqs = new Tech[] { w.getTechs()[7], w.getTechs()[12] };
            this.description = "Broadens the range of physiological traits your Demons can have, endowing some of them with amorphous forms that allow them to eventually rebuild themselves by drawing on the resentment of the defeated.  This delays the end of battle until the usual conditions (full extermination progress and no Chosen surrounded or captured) are fulfilled for two turns in a row.";
        }
        else if (index == 19) {
            this.name = "Causal Projection";
            this.prereqsRequired = 2;
            this.cost = 5;
            this.prereqs = new Tech[] { w.getTechs()[8], w.getTechs()[10] };
            this.description = "Grants you the ability to identify which civilians would, if removed from society, induce the greatest despair and sinful behavior in those close to them.  By withdrawing your forces while the Chosen are occupied and bringing those people with you, you can harvest a bit of energy from the general population.  This adds a Retreat option during battle which ends the fight prematurely, but grants +1 Evil Energy for every five full surround or capture turns added up among the Chosen.";
        }
        else if (index == 20) {
            this.name = "Intelligence";
            this.prereqsRequired = 2;
            this.cost = 5;
            this.prereqs = new Tech[] { w.getTechs()[9], w.getTechs()[14] };
            this.description = "Allows you to create Commanders capable of using a tremendous portion of your psychic capacity.  This comprehensive control will make it possible to more effectively subdue captured Chosen.  For +2 Evil Energy per deployment, your Commander's capture duration will increase by 1.";
        }
        else if (index == 21) {
            this.name = "Versatility";
            this.prereqsRequired = 2;
            this.cost = 5;
            this.prereqs = new Tech[] { w.getTechs()[6], w.getTechs()[15] };
            this.description = "Develops your ability to find common ground between two different aspects of the human mind and concentrate your psychic energy there in order to manifest both aspects at once.  For +5 Evil Energy per deployment, you can bypass the usual prohibition against putting a second Suppressor-class upgrade on your Commander.";
        }
        else if (index == 22) {
            this.name = "Ambition";
            this.prereqsRequired = 2;
            this.cost = 10;
            this.prereqs = new Tech[] { w.getTechs()[15], w.getTechs()[19] };
            this.description = "Deepens your connection with your Commander body, endowing it with your desire to propagate yourself across all sapient life.  Your body will be equipped with a monstrous phallus capable of penetrating and ejaculating inside the Chosen.  This is a 'Defiler'-class upgrade which is not normally compatible with other Suppressor- or Defiler-class upgrades.  It functions like the Inseminate action, including its ability to inflict Morality Break, but its improved effectiveness comes at the cost of 6 Evil Energy per deployment.";
        }
        else if (index == 23) {
            this.name = "Dominance";
            this.prereqsRequired = 2;
            this.cost = 10;
            this.prereqs = new Tech[] { w.getTechs()[12], w.getTechs()[16] };
            this.description = "Deepens your connection with your Commander body, endowing it with your desire to inflict mind-breaking pleasure until all resistance is unthinkable.  Your body will be equipped with an internal chamber which pulls Chosen inside and stimulates them to climax.  This is a 'Defiler'-class upgrade which is not normally compatible with other Suppressor- or Defiler-class upgrades.  It functions like the Force Orgasm action, including its ability to inflict Innocence Break, but its improved effectiveness comes at the cost of 6 Evil Energy per deployment.";
        }
        else if (index == 24) {
            this.name = "Spite";
            this.prereqsRequired = 2;
            this.cost = 10;
            this.prereqs = new Tech[] { w.getTechs()[14], w.getTechs()[18] };
            if (w.getGenderBalance()[2] == 0) {
                this.description = "Deepens your connection with your Commander body, endowing it with your desire to punish those who dare to stand in your way.  Your body will be equipped with several muscular arms which will torture and violate any Chosen they manage to get their hands on.  This is a 'Defiler'-class upgrade which is not normally compatible with other Suppressor- or Defiler-class upgrades.  It functions like the Sodomize action, including its ability to inflict Confidence Break, but its improved effectiveness comes at the cost of 6 Evil Energy per deployment.";
            }
            else if (w.getGenderBalance()[1] == 0 && w.getGenderBalance()[3] == 0) {
                this.description = "Deepens your connection with your Commander body, endowing it with your desire to punish those who dare to stand in your way.  Your body will be equipped with several muscular arms which will torture and violate any Chosen they manage to get their hands on.  This is a 'Defiler'-class upgrade which is not normally compatible with other Suppressor- or Defiler-class upgrades.  It functions like the Torture action, including its ability to inflict Confidence Break, but its improved effectiveness comes at the cost of 6 Evil Energy per deployment.";
            }
            else {
                this.description = "Deepens your connection with your Commander body, endowing it with your desire to punish those who dare to stand in your way.  Your body will be equipped with several muscular arms which will torture and violate any Chosen they manage to get their hands on.  This is a 'Defiler'-class upgrade which is not normally compatible with other Suppressor- or Defiler-class upgrades.  It functions like the Sodomize/Torture action, including its ability to inflict Confidence Break, but its improved effectiveness comes at the cost of 6 Evil Energy per deployment.";
            }
        }
        else if (index == 25) {
            this.name = "Vanity";
            this.prereqsRequired = 2;
            this.cost = 10;
            this.prereqs = new Tech[] { w.getTechs()[11], w.getTechs()[17] };
            this.description = "Deepens your connection with your Commander body, endowing it with your desire to leave no doubt as to your superiority over your foes.  Your body will be equipped with electronic tendrils which can hijack communications lines and send live footage of the battle to people who otherwise wouldn't be watching.  This is a 'Defiler'-class upgrade which is not normally compatible with other Suppressor- or Defiler-class upgrades.  It functions like the Broadcast action, including its ability to inflict Dignity Break, but its improved effectiveness comes at the cost of 6 Evil Energy per deployment.";
        }
        else if (index == 26) {
            this.name = "Genius";
            this.prereqsRequired = 2;
            this.cost = 10;
            this.prereqs = new Tech[] { w.getTechs()[13], w.getTechs()[20] };
            this.description = "Allows you to create Commanders capable of using a majority of your psychic capacity.  This optimized control will make it possible to more effectively subdue captured Chosen.  For +2 Evil Energy per deployment, your Commander's capture duration will increase by 1.";
        }
        else if (index == 27) {
            this.name = "Determination";
            this.prereqsRequired = 2;
            this.cost = 10;
            this.prereqs = new Tech[] { w.getTechs()[10], w.getTechs()[21] };
            this.description = "Develops your ability to tap into the psychic energy associated with aspects of the human mind.  Your Commander body will be correspondingly more pure and resilient.  For +5 Evil Energy per deployment, your Commander will be able to perform a third capture (provided that the battle does not end beforehand).";
        }
        else if (index == 28) {
            this.name = "Soul Resonance";
            this.prereqsRequired = 2;
            this.cost = 20;
            this.prereqs = new Tech[] { w.getTechs()[16], w.getTechs()[22] };
            this.description = "Spreads your ability to influence thoughts even beyond the Demonic hivemind.  Civilians in the area of Demonic raids will feel subconsciously motivated to resist the authorities' attempts to get them to evacuate.  Increases evacuation requirement from 160 to 200.";
        }
        else if (index == 29) {
            this.name = "Passion Release";
            this.prereqsRequired = 2;
            this.cost = 20;
            this.prereqs = new Tech[] { w.getTechs()[17], w.getTechs()[24] };
            this.description = "Modifies your human captives' brains so that they become mentally fixated on the power and supremacy of the Demons - and on the suffering and defeat of those who oppose them.  Their absolute confidence in your victory will produce psychic energy far disproportionate to their numbers, rivalling the power of the worldwide public.  Increases extermination requirement from 300 to 500.";
        }
        else if (index == 30) {
            this.name = "Reality Sealing";
            this.prereqsRequired = 2;
            this.cost = 20;
            this.prereqs = new Tech[] { w.getTechs()[18], w.getTechs()[26] };
            this.description = "Allows you to apply your mastery of psychic energy to directly influence the local laws of physics.  By channeling your power through your army, you can focus on creating a region where the Chosen find that reality itself conspires against them.  Until extermination is completed, the 'Do Nothing' action is replaced with 'Barrier', which increases all damage taken for the rest of the battle by 5% (compounding).";
        }
        else if (index == 31) {
            this.name = "Networked Consciousness";
            this.prereqsRequired = 2;
            this.cost = 20;
            this.prereqs = new Tech[] { w.getTechs()[19], w.getTechs()[23] };
            this.description = "Dissolves the distinction between different Thralls' minds and desires.  This will allow them to display supernatural reaction time and intelligence by relying on each other's senses and neural processing power.  Whenever the Thralls Surround one of the Chosen, you will immediately be able to give those Thralls another order on the same turn.";
        }
        else if (index == 32) {
            this.name = "Transcendence";
            this.prereqsRequired = 2;
            this.cost = 20;
            this.prereqs = new Tech[] { w.getTechs()[20], w.getTechs()[27] };
            this.description = "Greatly develops the depth of your control over the psychic energy associated with aspects of the human mind.  Your Commander body will be correspondingly more pure and resilient.  For +10 Evil Energy per deployment, your Commander will be able to perform a fourth capture (provided that the battle does not end beforehand).";
        }
        else if (index == 33) {
            this.name = "Synthesis";
            this.prereqsRequired = 2;
            this.cost = 20;
            this.prereqs = new Tech[] { w.getTechs()[21], w.getTechs()[25] };
            this.description = "Allows you to craft complex psychic constructs which use different layers of the human mind to reinforce one another.  For +10 Evil Energy per deployment, you can put both a Suppressor-class upgrade and a Defiler-class upgrade on the same Commander.";
        }
        else if (index == 34) {
            this.name = "Impregnation";
            this.prereqsRequired = 2;
            this.cost = 40;
            this.prereqs = new Tech[] { w.getTechs()[22], w.getTechs()[29] };
            this.description = "Optimizes your connection with your Commander body, endowing it with the ability to seed your offspring even in one of the Chosen";
            if (w.getGenderBalance()[2] > 0) {
                this.description = String.valueOf(this.description) + ", regardless of whether they're male or female";
            }
            this.description = String.valueOf(this.description) + ".  The targets' souls must be blackened with the Slaughter of many victims in order to be susceptible to this effect, but the resulting Total Morality Break as they're forced to bear an enemy of humanity will irrevocably sever their connection to society.  This is a Punisher-class upgrade which is not compatible with other Suppressor-, Defiler-, or Punisher-class upgrades, but it has no additional cost to apply and focuses strongly on FEAR and HATE damage.";
        }
        else if (index == 35) {
            this.name = "Hypnosis";
            this.prereqsRequired = 2;
            this.cost = 40;
            this.prereqs = new Tech[] { w.getTechs()[23], w.getTechs()[33] };
            this.description = "Optimizes your connection with your Commander body, endowing it with the ability to directly dominate the mind of the Chosen.  The targets must Fantasize on a regular basis in order to weaken their grasp of reality enough for this to work, but the resulting Total Innocence Break as they experience the ecstasy of the Demonic hivemind will render them permanently weaker to your temptations.  This is a Punisher-class upgrade which is not compatible with other Suppressor-, Defiler-, or Punisher-class upgrades, but it has no additional cost to apply and focuses strongly on DISG and PLEA damage.";
        }
        else if (index == 36) {
            this.name = "Drain";
            this.prereqsRequired = 2;
            this.cost = 40;
            this.prereqs = new Tech[] { w.getTechs()[24], w.getTechs()[28] };
            this.description = "Optimizes your connection with your Commander body, endowing it with the ability to absorb even the righteous energy of the Chosen.  The targets must be desperate enough for death to Detonate themselves over and over again or else they won't have the subconscious willingness that allows you to do this, but the resulting Total Confidence Break as they realize that they can only have peace by giving up their power will completely break their will to actually defeat you.  This is a Punisher-class upgrade which is not compatible with other Suppressor-, Defiler-, or Punisher-class upgrades, but it has no additional cost to apply and focuses strongly on ";
            if (w.tickle()) {
                this.description = String.valueOf(this.description) + "TICK and ANTI damage.";
            }
            else {
                this.description = String.valueOf(this.description) + "PAIN and INJU damage.";
            }
        }
        else if (index == 37) {
            this.name = "Parasitism";
            this.prereqsRequired = 2;
            this.cost = 40;
            this.prereqs = new Tech[] { w.getTechs()[25], w.getTechs()[30] };
            this.description = "Optimizes your connection with your Commander body, endowing it with the ability to latch on to a target's psychic signature and influence its physical manifestation.  The targets must be known to Striptease and perform other degrading acts in order for the effect to persist beyond your direct influence, but the resulting Total Dignity Break as the public sees the targets' sinful nature on full display will bring them within a hair's breadth of losing their Chosen powers.  This is a Punisher-class upgrade which is not compatible with other Suppressor-, Defiler-, or Punisher-class upgrades, but it has no additional cost to apply and focuses strongly on SHAM and EXPO.";
        }
        else if (index == 38) {
            this.name = "Mastery";
            this.prereqsRequired = 2;
            this.cost = 40;
            this.prereqs = new Tech[] { w.getTechs()[26], w.getTechs()[32] };
            this.description = "Perfects your control over the psychic energy associated with aspects of the human mind.  Your Commander body will reach maximum purity and resilience.  For +20 Evil Energy per deployment, your Commander will be able to perform a fifth capture.";
        }
        else if (index == 39) {
            this.name = "Relentlessness";
            this.prereqsRequired = 2;
            this.cost = 40;
            this.prereqs = new Tech[] { w.getTechs()[27], w.getTechs()[31] };
            this.description = "Extends your innate control over space and time to the region around your Commander body as well.  The Chosen will no longer be able to let their guard down after accumulating enough psychic energy to take flight.  For +10 Evil Energy per deployment, your Commander will be able to capture Chosen who are flying above the battlefield.";
        }
        else if (index == 40) {
            this.name = "Empathy";
            this.prereqsRequired = 3;
            this.cost = 100;
            this.prereqs = new Tech[] { w.getTechs()[31], w.getTechs()[34], w.getTechs()[38] };
            this.description = "Grants you subtle but significant access to the subconscious feelings of the Chosen, allowing you to cause them to relive their memories of their closest friends at critical points during combat.  During the final battle, the flashbacks will cause them to hesitate for an extra turn before dealing the killing blow to a surrounded ally who they have an Unbreakable Friendship with.";
        }
        else if (index == 41) {
            this.name = "Antipathy";
            this.prereqsRequired = 3;
            this.cost = 100;
            this.prereqs = new Tech[] { w.getTechs()[30], w.getTechs()[35], w.getTechs()[39] };
            this.description = "Grants you the ability to amplify the negative emotions of the Chosen, using their hatred for each other to weaken their powers and their will to fight.  At the start of the final battle, Chosen will take 10% Resolve damage for each Rival and 40% Resolve damage for each Bitter Enemy.";
        }
        else if (index == 42) {
            this.name = "Threaten+";
            this.prereqsRequired = 3;
            this.cost = 100;
            this.prereqs = new Tech[] { w.getTechs()[28], w.getTechs()[33], w.getTechs()[34] };
            this.description = "Perfects your telepathic abilities, allowing you to influence the Chosen by showing them frightening visions of what could be.  This doubles the damage of the Threaten action.  Additionally, during the final battle, the Threaten action will decrease the target's Resolve by a base value of " + w.threatenResolve() + "% per ally suffering from Demonic impregnation.  The decrease is multiplied by the target's FEAR level.";
        }
        else if (index == 43) {
            this.name = "Slime+";
            this.prereqsRequired = 3;
            this.cost = 100;
            this.prereqs = new Tech[] { w.getTechs()[29], w.getTechs()[32], w.getTechs()[35] };
            this.description = "Perfects the potency of your slime, allowing it to pull the Chosen into the Demonic hivemind.  This doubles the damage of the Slime action.  Additionally, during the final battle, the Slime action will decrease the Resolve of Hypnotized targets by " + w.slimeResolve() + "%.  The decrease is multiplied by the target's DISG level.";
        }
        else if (index == 44) {
            String actionName = "Attack";
            if (w.tickle()) {
                actionName = "Poke";
            }
            this.name = String.valueOf(actionName) + "+";
            this.prereqsRequired = 3;
            this.cost = 100;
            this.prereqs = new Tech[] { w.getTechs()[30], w.getTechs()[33], w.getTechs()[36] };
            this.description = "Perfects the combat ability of your Demons, allowing them to torture the Chosen with extreme precision.  This doubles the damage of the " + actionName + " action.  Additionally, during the final battle, the " + actionName + " action will decrease the Resolve of Drained targets by " + w.attackResolve() + "%, or non-Drained targets by 1%.  This decrease is multiplied by the target's ";
            if (w.tickle()) {
                this.description = String.valueOf(this.description) + "TICK level.";
            }
            else {
                this.description = String.valueOf(this.description) + "PAIN level.";
            }
        }
        else if (index == 45) {
            this.name = "Taunt+";
            this.prereqsRequired = 3;
            this.cost = 100;
            this.prereqs = new Tech[] { w.getTechs()[31], w.getTechs()[32], w.getTechs()[37] };
            this.description = "Perfects your connection with the collective consciousness of humanity, allowing you to cut off the flow of psychic energy the Chosen receive from the public.  This doubles the damage of the Taunt action.  Additionally, during the final battle, the Taunt action will decrease the Resolve of Parasitized targets who can be surrounded on that turn by " + w.tauntResolve() + "%.  This decrease is multiplied by the target's SHAM level.";
        }
        else if (index == 46) {
            this.name = "Enormity";
            this.prereqsRequired = 3;
            this.cost = 100;
            this.prereqs = new Tech[] { w.getTechs()[29], w.getTechs()[36], w.getTechs()[38] };
            this.description = "Allows you to create Commanders capable of using the entirety of your psychic capacity.  This flawless control will make it possible to more effectively subdue captured Chosen.  For +30 Evil Energy per deployment, your Commander's capture duration will increase by 2.";
        }
        else if (index == 47) {
            this.name = "Completion";
            this.prereqsRequired = 3;
            this.cost = 100;
            this.prereqs = new Tech[] { w.getTechs()[28], w.getTechs()[37], w.getTechs()[39] };
            this.description = "Enables you to craft constructs which embody the full depth of the human mental state.  For +60 Evil Energy per deployment, you can put one Suppressor-, one Defiler-, and one Punisher-class upgrade all on the same Commander.";
        }
        else if (index == 48) {
            this.name = "Imago Quickening";
            this.prereqsRequired = 1;
            this.cost = 300;
            this.prereqs = new Tech[] { w.getTechs()[42], w.getTechs()[43], w.getTechs()[44], w.getTechs()[45] };
            this.description = "Greatly hastens the corruption of the region of reality surrounding the city, but only at great cost and risk.  After buying this upgrade, the final battle will happen on the same day.  Failing to neutralize at least two of the three Chosen will result in them defeating you for good, so this upgrade should not be bought unless victory is absolutely certain!";
        }
    }
    
    public Boolean isOwned() {
        return this.owned;
    }
    
    public void printSummary(final WorldState w, final JTextPane t) {
        w.append(t, this.name);
        if (this.owned) {
            w.append(t, " (already owned)");
        }
        w.append(t, "\nCost: " + this.cost + " EE\nPrerequisites: ");
        for (int i = 0; i < this.prereqs.length; ++i) {
            if (i == this.prereqs.length - 1 && this.prereqsRequired == 1) {
                w.grayAppend(t, "or ");
            }
            if (this.prereqs[i].isOwned()) {
                w.append(t, this.prereqs[i].getName());
            }
            else {
                w.grayAppend(t, this.prereqs[i].getName());
            }
            if (i < this.prereqs.length - 1) {
                w.append(t, ", ");
            }
        }
        if (this.prereqs.length == 0) {
            w.append(t, "None");
        }
        w.append(t, "\nLeads to: ");
        int leadsCount = 0;
        int leadsCovered = 0;
        for (int j = 0; j < w.getTechs().length; ++j) {
            for (int k = 0; k < w.getTechs()[j].getPrereqs().length; ++k) {
                if (w.getTechs()[j].getPrereqs()[k] == this) {
                    ++leadsCount;
                }
            }
        }
        for (int j = 0; j < w.getTechs().length; ++j) {
            for (int k = 0; k < w.getTechs()[j].getPrereqs().length; ++k) {
                if (w.getTechs()[j].getPrereqs()[k] == this) {
                    if (leadsCovered != 0) {
                        w.append(t, "; ");
                    }
                    ++leadsCovered;
                    w.append(t, w.getTechs()[j].getName());
                    w.append(t, " (with ");
                    int withCovered = 0;
                    for (int l = 0; l < w.getTechs()[j].getPrereqs().length; ++l) {
                        if (k != l) {
                            if (withCovered > 0) {
                                w.append(t, ", ");
                            }
                            if ((l == w.getTechs()[j].getPrereqs().length - 1 || (l == w.getTechs()[j].getPrereqs().length - 2 && w.getTechs()[j].getPrereqs()[w.getTechs()[j].getPrereqs().length - 1] == this)) && w.getTechs()[j].prereqsRequired == 1) {
                                w.append(t, "or ");
                            }
                            ++withCovered;
                            if (w.getTechs()[j].getPrereqs()[l].isOwned()) {
                                w.append(t, w.getTechs()[j].getPrereqs()[l].getName());
                            }
                            else {
                                w.grayAppend(t, w.getTechs()[j].getPrereqs()[l].getName());
                            }
                        }
                    }
                    w.append(t, ")");
                }
            }
        }
        if (leadsCount == 0) {
            w.append(t, "None");
        }
        w.append(t, "\n" + this.description);
    }
    
    public String getName() {
        return this.name;
    }
    
    public Tech[] getPrereqs() {
        return this.prereqs;
    }
    
    public Tech() {
        this.prereqsRequired = 1;
        this.cost = 1;
        this.name = "";
        this.description = "";
        this.tooltip = "";
        this.prereqs = new Tech[0];
        this.owned = false;
    }
}
