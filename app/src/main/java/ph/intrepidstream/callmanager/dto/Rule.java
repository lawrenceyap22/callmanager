package ph.intrepidstream.callmanager.dto;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ph.intrepidstream.callmanager.util.RuleState;

public class Rule implements Parcelable {
    private Long id;
    private String name;
    private RuleState state;
    private boolean isAppGenerated;
    private List<Condition> conditions;

    public Rule() {
    }

    public Rule(Rule other) {
        this.id = other.id;
        this.name = other.name;
        this.state = other.state;
        this.isAppGenerated = other.isAppGenerated();
        this.conditions = new ArrayList<>(other.conditions);
    }

    protected Rule(Parcel in) {
        id = in.readLong();
        name = in.readString();
        state = RuleState.valueOf(in.readString());
        isAppGenerated = in.readInt() != 0;
        conditions = in.createTypedArrayList(Condition.CREATOR);
    }

    public static final Parcelable.Creator<Rule> CREATOR = new Creator<Rule>() {
        @Override
        public Rule createFromParcel(Parcel source) {
            return new Rule(source);
        }

        @Override
        public Rule[] newArray(int size) {
            return new Rule[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(state.name());
        dest.writeInt(isAppGenerated ? 1 : 0);
        dest.writeTypedList(conditions);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RuleState getState() {
        return state;
    }

    public void setState(RuleState state) {
        this.state = state;
    }

    public boolean isAppGenerated() {
        return isAppGenerated;
    }

    public void setIsAppGenerated(boolean isAppGenerated) {
        this.isAppGenerated = isAppGenerated;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    public boolean isIncluded(String number) {
        boolean included = false;
        Iterator<Condition> conditionIterator = conditions.iterator();
        Condition condition;
        while (!included && conditionIterator.hasNext()) {
            condition = conditionIterator.next();
            if (number.startsWith(condition.getNumber())) {
                included = true;
            }
        }
        return included;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Rule rule = (Rule) o;

        return new EqualsBuilder()
                .append(id, rule.id)
                .append(name, rule.name)
                .append(state, rule.state)
                .append(isAppGenerated, rule.isAppGenerated)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(state)
                .append(isAppGenerated)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("name", name)
                .append("state", state)
                .append("isAppGenerated", isAppGenerated)
                .toString();
    }

}
