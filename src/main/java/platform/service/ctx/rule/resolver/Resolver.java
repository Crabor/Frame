package platform.service.ctx.rules.resolvers;

public class Resolver {
    private ResolverType resolverType;
    private String variable;
    private String value;


    public void setResolverType(ResolverType resolverType) {
        this.resolverType = resolverType;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ResolverType getResolverType() {
        return resolverType;
    }

    public String getVariable() {
        return variable;
    }

    public String getValue() {
        return value;
    }
}
