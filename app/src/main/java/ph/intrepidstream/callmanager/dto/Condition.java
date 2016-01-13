package ph.intrepidstream.callmanager.dto;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import ph.intrepidstream.callmanager.util.ConditionLookup;

public class Condition implements Parcelable {
    private Long id;
    private Long ruleId;
    private ConditionLookup lookup;
    private String number;

    public Condition(){

    }

    protected Condition(Parcel in) {
        id = in.readLong();
        ruleId = in.readLong();
        lookup = ConditionLookup.valueOf(in.readString());
        number = in.readString();
    }

    public static final Creator<Condition> CREATOR = new Creator<Condition>() {
        @Override
        public Condition createFromParcel(Parcel in) {
            return new Condition(in);
        }

        @Override
        public Condition[] newArray(int size) {
            return new Condition[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(ruleId);
        dest.writeString(lookup.name());
        dest.writeString(number);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public ConditionLookup getLookup() {
        return lookup;
    }

    public void setLookup(ConditionLookup lookup) {
        this.lookup = lookup;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Condition that = (Condition) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(ruleId, that.ruleId)
                .append(lookup, that.lookup)
                .append(number, that.number)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(ruleId)
                .append(lookup)
                .append(number)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("ruleId", ruleId)
                .append("lookup", lookup)
                .append("number", number)
                .toString();
    }
}
