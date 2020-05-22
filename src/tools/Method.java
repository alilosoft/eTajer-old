/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

/**
 *
 * @author lenovooo
 */
public class Method {

    private String name = "Not Defined";
    private Object[] args = null;

    public Method(String name) {
        this.name = name;
    }

    public Method(String name, Object[] args) {
        this(name);
        this.args = args;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Object[] getArgs() {
        return args;
    }

    public String getDescription() {
        String desc = "Method: " + this.name;
        String argsDesc = "";
        if (args != null && args.length > 0) {
            int argNbr = 1;
            for (Object arg : args) {
                argsDesc = "\nParam " + argNbr++ + ":" + arg.toString();
            }
        }
        return desc + argsDesc;
    }
}
