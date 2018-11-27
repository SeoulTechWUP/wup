package wup.data;

import java.util.Date;

/**
 * 일정 정보를 관리하는 클래스입니다.
 *
 * @author Eunbin Jeong
 */
public class Schedule implements TodoListOwner {
    private int id;
    private Date createdAt;
    private Date modifiedAt;
    private Planner planner;
    private String title;
    private String description;
    private String location;
    private Date startsAt;
    private Date endsAt;
    private boolean allDay;
    // private object[] labels;

    @Override
    public int getId() {
        return id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public Planner getPlanner() {
        return planner;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public Date getStartsAt() {
        return startsAt;
    }

    public Date getEndsAt() {
        return endsAt;
    }

    public boolean getAllDay() {
        return allDay;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public void setPlanner(Planner planner) {
        this.planner = planner;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setStartsAt(Date startsAt) {
        this.startsAt = startsAt;
    }

    public void setEndsAt(Date endsAt) {
        this.endsAt = endsAt;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }
}
