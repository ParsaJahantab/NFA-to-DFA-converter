import java.util.*;

class NFA_Node {
    String name;
    boolean init=false;
    boolean finals=false;
    HashMap<String, HashSet<NFA_Node>> transition;
    HashSet<NFA_Node> epsilon;
    HashMap<String,HashSet<NFA_Node>> transition_with_epsilon;
    NFA_Node(String name)
    {
        this.name = name;
        epsilon=new HashSet<>();
        transition= new HashMap<>();
        transition_with_epsilon= new HashMap<>();
        transition.putIfAbsent("-",new HashSet<>());
        transition.get("-").add(this);
        epsilon.add(this);
    }



    void add_to_trans(String transitor, NFA_Node state)
    {
        transition.putIfAbsent(transitor,new HashSet<>());
        transition.get(transitor).add(state);
    }
    void create_epsilon(NFA_Node addto, NFA_Node addfrom,HashSet<NFA_Node> hashSet,HashSet<String> final_states)
    {
        for (NFA_Node NFA_node : addfrom.transition.get("-")) {
            if (!(hashSet.contains(NFA_node))) {
                if (NFA_node.finals)
                {
                    addto.finals=true;
                    final_states.add(addto.name);
                }
                if (addfrom.finals)
                {
                    NFA_node.finals=true;
                    final_states.add(NFA_node.name);
                }
                addto.epsilon.add(NFA_node);
                hashSet.add(addfrom);
                create_epsilon(addto, NFA_node, hashSet,final_states);
            }
        }
    }
    void create_transition_with_epsilon(NFA_Node addto, NFA_Node addfrom)
    {
        for (NFA_Node NFA_node : addfrom.epsilon) {

            for (String key : NFA_node.transition.keySet()) {
                if (!(key.equals("-"))) {
                    Iterator<NFA_Node> transition_iterate = NFA_node.transition.get(key).iterator();
                    addto.transition_with_epsilon.putIfAbsent(key, new HashSet<>());
                    while (transition_iterate.hasNext()) {
                        NFA_Node NFA_node1 = transition_iterate.next();
                        addto.transition_with_epsilon.get(key).add(NFA_node1);
                    }
                }
            }

        }
    }
}
class DFA_Node{
    String name1;
    String name2;
    HashSet<NFA_Node> nfa_nodes;
    DFA_nodes dfa_nodes;
    HashMap<String,DFA_Node> transition;
    boolean init;
    boolean finals;
    boolean checked;
    DFA_Node(String name1,String name2,HashSet<NFA_Node> nfa_nodes,DFA_nodes dfa_nodes)
    {
        this.name1=name1;
        this.name2=name2;
        this.nfa_nodes=nfa_nodes;
        this.dfa_nodes=dfa_nodes;
        transition=new HashMap<>();
        init=false;
        finals=false;
        checked=false;

    }
    void Complete_DFA_Node()
    {
        for (String str : dfa_nodes.inputs) {
            if (!str.equals("-")) {
                HashSet<NFA_Node> combined = dfa_nodes.combine_for_an_input(nfa_nodes, str);
                //System.out.println(name1+" size "+combined.size());
                DFA_Node dfa_node = dfa_nodes.create(combined);
                transition.put(str, dfa_node);
                dfa_nodes.trans_counter = dfa_nodes.trans_counter + 1;
            }
        }
        for (NFA_Node node:nfa_nodes) {
            if (node.finals)
            {
                finals=true;
                dfa_nodes.final_counter=dfa_nodes.final_counter+1;
                break;
            }
        }
        /*for (String str: dfa_nodes.final_states) {
            if (name1.contains(str))
            {
                finals=true;
                dfa_nodes.final_counter=dfa_nodes.final_counter+1;
                break;
            }
        }*/
        checked=true;
    }
}
class DFA_nodes{
    int state_counter;
    int trans_counter;
    int final_counter;
    DFA_Node init_state;
    HashMap<String,DFA_Node> DFA_States;
    HashMap<String,DFA_Node> DFA_States2;
    HashSet<String> inputs;
    HashSet<String> final_states;
    DFA_nodes(HashSet<String> inputs,HashSet<String> final_states)
    {
        state_counter=0;
        trans_counter=0;
        final_counter=0;
        DFA_States=new HashMap<>();
        DFA_States2=new HashMap<>();
        this.inputs=inputs;
        this.final_states=final_states;
    }
    void Create_DFA()
    {
        int flag=0;
        for (DFA_Node dfa_node : DFA_States.values()) {
            if (!dfa_node.checked) {
                flag = 1;
                dfa_node.Complete_DFA_Node();
                break;
            }
        }
        if (flag==1)
        {
            Create_DFA();
        }
    }
    DFA_Node create(HashSet<NFA_Node> hashSet)
    {
        Iterator<NFA_Node> it1= hashSet.iterator();
        int [] arr2=new int[hashSet.size()];
        int j=0;
        while (it1.hasNext())
        {
            NFA_Node node=it1.next();
            arr2[j]=Integer.parseInt(node.name);
            j=j+1;
        }
        Arrays.sort(arr2);
        String[] arr=new String[hashSet.size()];
        for (int i = 0; i < arr2.length; i++) {
            StringBuilder stringBuilder=new StringBuilder();
            String s;
            if (i==arr2.length-1) {
                s = stringBuilder.append(String.valueOf(arr2[i])).toString();
            }
            else
            {
                s = stringBuilder.append(String.valueOf(arr2[i])).append(",").toString();
            }
            arr[i]=s;
        }
        String key=String.join("",arr);
        if (!(DFA_States.containsKey(key)))
        {
            state_counter=state_counter+1;
            String name2=String.valueOf(state_counter);
            DFA_Node dfa_node=new DFA_Node(key,name2,hashSet,this);
            DFA_States.put(key,dfa_node);
            DFA_States2.put(name2,dfa_node);
            if (state_counter==1)
            {
                dfa_node.init=true;
                init_state=dfa_node;
            }
            return dfa_node;
        }
        else
        {
            return DFA_States.get(key);
        }

    }
    void combine_for_DFA_init(HashSet<NFA_Node> NFA_init_states)
    {
        HashSet<NFA_Node> DFA_init=new HashSet<>();
        for (NFA_Node node : NFA_init_states) {
            DFA_init.addAll(node.epsilon);
        }
        /*for (NFA_Node node:DFA_init) {
            System.out.print(node.name+" ");
        }*/
        DFA_Node dfa_node=create(DFA_init);
        //dfa_node.Complete_DFA_Node();

        Create_DFA();
    }
    public HashSet<NFA_Node> combine_for_an_input(HashSet<NFA_Node> NFA_states,String input)
    {
        HashSet<NFA_Node> combined=new HashSet<>();
        for (NFA_Node node : NFA_states) {
            //System.out.println(input+" "+nfa_node.name);
            if (node!=null && node.transition_with_epsilon.containsKey(input) ) {
                for (NFA_Node node1 : node.transition_with_epsilon.get(input)) {
                    if (node1!=null)
                        combined.add(node1);
                }
            }
            //combined.addAll(node.transition_with_epsilon.get(input));

        }
        return combined;
    }
}
public class Main {

    public static void main(String[] args) {
        String str;
        Scanner input=new Scanner(System.in);
        str=input.nextLine();
        String[] arrOfStr=str.split(" ");
        HashMap<String, NFA_Node> NFA_Nodes= new HashMap<>();
        HashSet<String> final_states=new HashSet<>();
        HashSet<NFA_Node> init_states=new HashSet<>();
        int n=Integer.parseInt(arrOfStr[0]);
        HashSet<String> inputs=new HashSet<>();
        for (int i = 1; i <= n; i++) {
            String s=Integer.toString(i);
            NFA_Node NFA_node =new NFA_Node(s);
            NFA_Nodes.put(s, NFA_node);
        }
        int m=Integer.parseInt(arrOfStr[1]);
        int s=Integer.parseInt(arrOfStr[2]);
        int q=Integer.parseInt(arrOfStr[3]);
        String str1=input.nextLine();
        String[] arrOfStr1=str1.split(" ");
        for (int i = 1; i <= n; i++) {
            if (arrOfStr1[i-1].equals("1"))
            {
                String s1=Integer.toString(i);
                NFA_Nodes.get(s1).finals=true;
                final_states.add(NFA_Nodes.get(s1).name);
            }
        }
        String str2=input.nextLine();
        String[]arrOfStr2=str2.split(" ");
        for (int i = 1; i <= s; i++) {
            String s1=arrOfStr2[i-1];
            NFA_Nodes.get(s1).init=true;
            init_states.add(NFA_Nodes.get(s1));
        }
        for (int i = 1; i <= m; i++) {
            String str3=input.nextLine();
            String[] arrOfStr3=str3.split(" ");
            if (!inputs.equals("-"))
                inputs.add(arrOfStr3[0]);
            NFA_Nodes.get(arrOfStr3[1]).add_to_trans(arrOfStr3[0],NFA_Nodes.get(arrOfStr3[2]));
        }
        for (int i = 1; i <= NFA_Nodes.size(); i++) {
            NFA_Node node= NFA_Nodes.get(String.valueOf(i));
            HashSet<NFA_Node> nfa_nodeHashSet = new HashSet<>();
            nfa_nodeHashSet.add(node);
            node.create_epsilon(node, node, nfa_nodeHashSet,final_states);
            node.create_transition_with_epsilon(node, node);
        }
        /// NFA states epsilon and transition test
        /*for (NFA_Node node : NFA_Nodes.values()) {
            HashSet<NFA_Node> nfa_nodeHashSet = new HashSet<>();
            nfa_nodeHashSet.add(node);
            node.create_epsilon(node, node, nfa_nodeHashSet);
            node.create_transition_with_epsilon(node, node);
        }*/
        /*for (NFA_Node nfanode:NFA_Nodes.values())
        {
            for (NFA_Node nfa_node:nfanode.epsilon) {
                System.out.println(nfanode.name+"   "+nfa_node.name);
            }
        }
        System.out.println();
        System.out.println();
        System.out.println();
        for (NFA_Node nfanode:NFA_Nodes.values()) {
            for (String sy:nfanode.transition_with_epsilon.keySet()) {
                HashSet hashSet=nfanode.transition_with_epsilon.get(sy);
                Iterator<NFA_Node> nfaNodeIterator= hashSet.iterator();
                while (nfaNodeIterator.hasNext())
                {
                    NFA_Node node=nfaNodeIterator.next();
                    System.out.println(nfanode.name+" "+nfanode.finals+" "+sy+" "+node.name+" "+node.finals);
                }
            }
        }
        System.out.println();
        System.out.println();*/
        String [] tests=new String[q];
        for (int i = 0; i < q; i++) {
            tests[i]=input.nextLine();
        }
        DFA_nodes dfa_nodes=new DFA_nodes(inputs,final_states);
        dfa_nodes.combine_for_DFA_init(init_states);
        System.out.println(dfa_nodes.state_counter+" "+ dfa_nodes.trans_counter+" "+ dfa_nodes.final_counter);
        for (int i = 1; i <= dfa_nodes.DFA_States2.size(); i++) {
            DFA_Node dfa_node=dfa_nodes.DFA_States2.get(String.valueOf(i));
            if (dfa_node.finals)
            {
                System.out.print("1 ");
            }
            else
            {
                System.out.print("0 ");
            }
        }
        System.out.println();
        for (int i = 1; i <= dfa_nodes.DFA_States2.size(); i++) {
            DFA_Node dfa_node=dfa_nodes.DFA_States2.get(String.valueOf(i));
            for (String key:dfa_node.transition.keySet()) {
                DFA_Node dfa_node1=dfa_node.transition.get(key);
                System.out.println(key+" "+dfa_node.name2+" "+dfa_node1.name2);
            }
        }
        for (String test : tests) {
            StringBuilder stringBuilder = new StringBuilder();
            DFA_Node dfa_node = dfa_nodes.init_state;
            for (int j = 0; j < test.length(); j++) {
                if (!test.equals("-")) {
                    dfa_node = dfa_node.transition.get(String.valueOf(test.charAt(j)));
                    stringBuilder.append(dfa_node.name2).append(" ");
                }
            }
            if (dfa_node.finals) {
                System.out.println("Yes " + stringBuilder);
            } else {
                System.out.println("No " + stringBuilder);
            }
        }
        //Iterator<NFA_Node> iterator1=NFA_Nodes.values().iterator();
        //Iterator<NFA_Node> iterator1=hashSet.iterator();
        /*while (iterator1.hasNext())
        {
            NFA_Node node= iterator.next();
            Iterator<String> iterator2=node.transition.keySet().iterator();
            while (iterator2.hasNext())
            {
                String s1= iterator2.next();
                NFA_Node []arr= (NFA_Node[]) node.transition.get(s1).toArray();
                for (int i = 0; i < arr.length; i++) {
                    System.out.println(arr[i].name);
                }
            }

        }*/




    }
}
