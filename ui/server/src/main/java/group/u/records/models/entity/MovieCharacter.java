package group.u.records.models.entity;

import group.u.records.models.Person;

import java.util.HashSet;
import java.util.Set;

public class MovieCharacter {

    private String name;
    private String character;
    private String photo_img;

    public String getActor_id() {
        return actor_id;
    }

    private String actor_id;

    public String getName() {
        return name;
    }

    public String getCharacter() {
        return character;
    }

    public String getPhoto_img() {
        return photo_img;
    }

    public Person toPerson(){
        return new Person(actor_id, name, Set.of(character), new HashSet<>());
    }
}
