package com.scoresheet.discgolf;

/**
 * Created by Joe Post on 4/12/2017.
 */

public class SingleRoundScoringTable {

    //this class will be created for every round scoring table
    //user will add holes to the single round scoring table until they are done with the course

    String id;
    String course_table_ID;
    String hole;
    String par;
    String score;
    String done;
    String name;

    // constructors
    public SingleRoundScoringTable(){
    }

    public SingleRoundScoringTable(String hole, String par, String score) {

        this.course_table_ID = course_table_ID;
        this.hole = hole;
        this.par = par;
        this.score = score;
    }
    //setters

    public void setID(String id){
        this.id = id;
    }

    public void setCourseID(String course_id){
        this.course_table_ID = course_id;
    }

    public void setName(String name) { this.name = name; }

    public void setHoleNumber(String hole){
        this.hole = hole;
    }

    public void setParNumber(String par){
        this.par = par;
    }

    public void setScoreNumber(String score){
        this.score = score;
    }

    public void setDone(String done) { this.done = done; }



    // getters
    public String getHole() {
        return this.hole;
    }

    public String getPar() {
        return this.par;
    }

    public String getScore() {
        return this.score;
    }

    public String getDone() { return this.done;  }

    public String getName() { return this.name; }




}
