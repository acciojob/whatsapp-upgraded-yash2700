package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Repository
public class WhatsappRepository {

    private HashMap<String,User> userHashMap;
    private HashMap<String,Group> groupHashMap;

    private HashMap<String,List<User>> groupMemHashMap;

    private HashMap<String,List<Message>> messageHashMap;

    private HashMap<User,List<Message>> userMessageHashMap;

    private HashMap<String,User> adminHashMap;

    private int groupCount=0;

    private int id=0;

    public WhatsappRepository() {
        this.userHashMap = new HashMap<>();
        this.groupHashMap = new HashMap<>();
        this.messageHashMap = new HashMap<>();
        this.groupMemHashMap= new HashMap<>();
        this.userMessageHashMap = new HashMap<>();
        this.adminHashMap = new HashMap<>();
    }

    public String createUser(String name, String mobile) throws Exception {
        if(!userHashMap.containsKey(mobile)) {
            userHashMap.put(mobile, new User(name, mobile));
            return "SUCCESS";
        }
        else{
            throw new Exception("User already exists");
        }
    }

    public Group createGroup(List<User> users) throws Exception{

        Group group;
        if(users.size()==2){
            group = new Group(users.get(1).getName(),2);
        }
        else{
            groupCount++;
            String name= "Group "+groupCount;
            group = new Group(name,users.size());
        }
        groupHashMap.put(group.getName(),group);
        groupMemHashMap.put(group.getName(),users);
        adminHashMap.put(group.getName(),users.get(0));

        return group;
    }

    public int createMessage(String content){

        id++;
        Message message = new Message(id,content);
        return message.getId();
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception{

        if(!groupHashMap.containsKey(group.getName())){
            throw new Exception("Group does not exist");
        }

        List<User> users = groupMemHashMap.get(group.getName());
        boolean userFound = false;

        if(users!=null){
            for(User user:users){
                if(user.equals(sender)){
                    userFound=true;
                    break;
                }
            }
        }

        if(userFound==false){
            throw new Exception("You are not allowed to send message");
        }

        List<Message> messageList = null;

        if(messageHashMap.containsKey(group.getName()))
            messageList = messageHashMap.get(group.getName());

        if(messageList==null)
            messageList=new ArrayList<>();

        messageList.add(message);

        messageHashMap.put(group.getName(),messageList);

        List<Message> userMessageList = null;

        if(userMessageHashMap.containsKey(sender))
            userMessageList=userMessageHashMap.get(sender);

        if(userMessageList==null)
            userMessageList=new ArrayList<>();

        userMessageList.add(message);

        userMessageHashMap.put(sender,userMessageList);

        return messageList.size();
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception{

        if(!groupHashMap.containsKey(group.getName())){
            throw new Exception("Group does not exist");
        }

        if(adminHashMap.get(group.getName())!=approver){
            throw new Exception("Approver does not have rights");
        }

        List<User> users = groupMemHashMap.get(group.getName());
        boolean userFound = false;

        if(users!=null){
            for(User user1:users){
                if(user1.equals(user)){
                    userFound=true;
                    break;
                }
            }
        }

        if(userFound==false){
            throw new Exception("User is not a participant");
        }

        adminHashMap.put(group.getName(),user);
        return "SUCCESS";
    }

    public int removeUser(User user) throws Exception{

        boolean userFound =false;
        for(List<User> users : groupMemHashMap.values()){
            if(users.contains(user))
            {
                userFound=true;
                break;
            }
        }
        if(userFound==false)
        {
            throw new Exception("User not found");
        }

        for(User admin : adminHashMap.values()){
            if(admin.equals(user)){
                throw new Exception("Cannot remove admin");
            }
        }

        String groupName = "";

        for(String name : groupMemHashMap.keySet()){
            List<User> users = groupMemHashMap.get(name);
            if(users.contains(user)){
                groupName = name;
                break;
            }
        }

        List<User> users = groupMemHashMap.get(groupName);

        users.remove(user);

        groupMemHashMap.put(groupName,users);

        Group group = groupHashMap.get(groupName);

        group.setNumberOfParticipants(users.size());

        groupHashMap.put(groupName,group);

        userHashMap.remove(user);

        List<Message> messageList = messageHashMap.get(groupName);

        for(Message message:userMessageHashMap.get(user)){
            if(messageList.contains(message)){
                messageList.remove(message);
            }
        }

        messageHashMap.put(groupName,messageList);

        int sum = messageList.size()+users.size();

        userMessageHashMap.remove(user);

        for(List<Message> msg : messageHashMap.values()){
            sum=sum+ msg.size();
        }

        return sum;
    }

    public String findMessage(Date start, Date end, int K) {
        return "";
    }
}