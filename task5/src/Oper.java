public enum Oper {
    NONE, NOT, AND, OR, IMPL;

    @Override
    public String toString() {
        switch (this){
            case NONE:
                return "";
            case OR:
                return "|";
            case AND:
                return "&";
            case NOT:
                return "!";
            case IMPL:
                return "->";
            default:
                return "";
        }
    }
}
