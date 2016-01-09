package ph.intrepidstream.callmanager.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

import ph.intrepidstream.callmanager.util.RuleState;

public class Rule {
    private Long id;
    private String name;
    private RuleState state;
    private boolean isAppGenerated;
    private List<Condition> conditions;

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
