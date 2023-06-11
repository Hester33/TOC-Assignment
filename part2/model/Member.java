package part2.model;

public class Member {
    public String id;
    public String name;
    public String image;
    public String participation;

    public Member(String id, String name, String image, String participation) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.participation = participation;
    }

    @Override
    public String toString() {
        return id + ", " + name + ", " + image + ", " + participation;
    }
}
