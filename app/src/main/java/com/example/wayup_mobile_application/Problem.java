package com.example.wayup_mobile_application;

import android.database.Cursor;
import android.widget.Toast;

import java.util.ArrayList;

public class Problem {

    public int id;
    public String sequence;
    public String sequence_counters;
    public String sequence_tags;
    public String name;
    public String grade;
    public String setter;
    public String comment;

    public Problem(int id, String sequence, String sequence_tags,String sequence_counters, String name, String grade, String setter, String comment)
    {
        this.id = id;
        this.sequence = sequence;
        this.sequence_tags = sequence_tags;
        this.sequence_counters = sequence_counters;
        this.name = name;
        this.grade = grade;
        this.setter = setter;
        this.comment = comment;
    }

    public int getId()
    {
        return id;
    }
    public String getSequence()
    {
        return sequence;
    }
    public String getSequence_tags()
    {
        return sequence_tags;
    }
    public String getSequence_counters()
    {
        return sequence_counters;
    }
    public String getName()
    {
        return name;
    }
    public String getGrade()
    {
        return grade;
    }
    public String getSetter()
    {
        return setter;
    }
    public String getComment()
    {
        return comment;
    }

}
