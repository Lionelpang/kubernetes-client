package io.fabric8.kudo.client.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author pangms
 * @date 2020/4/24
 */
public class Tree {

    private String msg;

    private int index = 0;

    private List<Tree> nodes = new ArrayList<>();

    private Tree(String msg) {
        this.msg = msg;
        this.index = 0;
    }

    private Tree(String msg, int index) {
        this.msg = msg;
        this.index = index;
    }

    public static Tree craeteRoot(String name) {
        return new Tree(name);
    }

    public Tree nodeNode(String name) {
        Tree node = new Tree(name, this.index++);
        this.nodes.add(node);
        return node;
    }

    public String msg() {
        return msg;
    }

    public int index() {
        return this.index;
    }

    public String printTree() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s \r\n", this.msg()));

        Stack<Tree> stack = new Stack<>();
        stack.push(this);//将更节点入栈
        while (!stack.isEmpty()) {
            //出栈
            Tree node = stack.pop();
            // 添加节点信息
            sb.append(String.format("%s \r\n", addTabForMsg(node.msg(), node.index())));

            if (node.nodes != null && node.nodes.size() > 0) {
                node.nodes.forEach(n -> stack.push(n));
            }
        }
        return sb.toString();
    }

    private static final String Tab = " ";
    private String addTabForMsg(String msg, int index) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < index; i++) {
            sb.append(Tab);
        }
        return sb.toString() + msg;
    }
}