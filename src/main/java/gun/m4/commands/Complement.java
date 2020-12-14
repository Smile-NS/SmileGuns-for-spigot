package gun.m4.commands;

import gun.m4.gun.GunType;

import java.util.*;

public class Complement {

    private final String[] args;

    public Complement(String[] args){
        this.args = args;
    }

    public List<String> gun(){
        if (args.length == 1 ) return selectArgs(0, new ArrayList<>(Arrays.asList("give")));

        if (args[0].equalsIgnoreCase("give")
                && args.length == 2) return selectArgs(1, gunTypeArgs(GunType.values()));

        return null;
    }

    private List<String> selectArgs(int index, List<String> list){
        if (args[index].length() == 0) return list;
        else return expansionArgs(index, list);
    }

    private List<String> expansionArgs(int index, List<String> list){
        for (String str : list){
            if (str.startsWith(args[index]))
                return Collections.singletonList(str);
        }
        return null;
    }

    private List<String> gunTypeArgs(GunType[] typeList){
        List<String> result = new ArrayList<>();
        for (GunType type : typeList){
            String str = type.name().toLowerCase();
            result.add(str);
        }
        return result;
    }
}
