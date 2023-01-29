package com.driver;

import org.springframework.stereotype.Repository;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Repository
public class WhatsappRepository {

    private static int groupCount=0;
    private static int messageCount=0;
    HashMap<String,User> userList=new HashMap<>();
    HashMap<Group,List<User>>   groupList=new HashMap<>();

    HashMap<Group,List<Message>> groupMessages=new HashMap<>();

    List<Message> messageList=new ArrayList<>();

    HashMap<User,List<Message>> userMessages=new HashMap<>();

    public String createUser(String name,String mobile)throws Exception{
            if(userList.containsKey(name)){
                throw new Exception("user already exists");
            }
            else {
                userList.put(name,new User(name,mobile));
            }
            return "SUCCESS";
    }

    public Group createGroup(List<User> users){
        if(groupList.size()==2){
            Group group=new Group(users.get(1).getName(),2);
            groupList.put(group,users);
            return group;
        }
        Group group=new Group("Group "+ ++groupCount,users.size());
        groupList.put(group,users);
        return group;
    }

    public int createMessage(String content){
        Message message=new Message(++messageCount,content);
        message.setTimestamp(new Date());
        messageList.add(message);
        return messageCount;
    }

    public int sendMessage(Message message,User sender,Group group) throws Exception{
            if(!groupList.containsKey(group.getName()))
                throw new RuntimeException("Group does not exist");

            int i=0;
            for(User user:groupList.get(group)) {
                if(sender.equals(user)){
                    i++;
                    break;
                }
            }
            if(i==0)
                throw new RuntimeException("You are not alloved to send message");

            if(groupMessages.containsKey(group)){
                groupMessages.get(group).add(message);
            }
            else{
                List<Message> messages=new ArrayList<>();
                messages.add(message);
                groupMessages.put(group,messages);
            }
            if(userMessages.containsKey(sender)){
                userMessages.get(sender).add(message);
            }
            else{
                List<Message> messages=new ArrayList<>();
                messages.add(message);
                userMessages.put(sender,messages);
            }
            return groupMessages.get(group).size();
    }

    public String changeAdmin(User approver,User user,Group group)throws Exception{
        if(!groupList.containsKey(group))
            throw new RuntimeException("Group does not exist");
        if(groupList.get(group).get(0)!=approver)
            throw new RuntimeException("Approver does not have rights");
        if(!groupList.get(group).contains(user))
            throw new RuntimeException("User is not a paticipant");

        groupList.get(group).remove(user);
        groupList.get(group).add(0,user);
        return "SUCCESS";
    }

    public int removeUser(User user)throws Exception{

        int i=0;
        Group requiredGroup=null;
        for(Group group:groupList.keySet()){
            for(User users:groupList.get(group)){
                if(users.equals(user)){
                    requiredGroup=group;
                    i=1;
                    break;
                }
            }
        }
        if(i==0)
            throw new RuntimeException("User not found");
        if(groupList.get(requiredGroup).get(0)==user)
            throw new RuntimeException("Cannot remove admin");

        for(Group group:groupList.keySet()){
            for(Message message:groupMessages.get(group))
                if(userMessages.get(user).contains(message)){
                    groupMessages.get(group).remove(message);
                }
            }

        for(Message message:messageList){
            if(userMessages.get(user).contains(message)){
                messageList.remove(message);
            }
        }
        groupList.get(requiredGroup).remove(user);
        userMessages.remove(user);
        return groupList.get(requiredGroup).size()+groupMessages.get(requiredGroup).size()+messageList.size();

        }
        public String findMessage(Date start,Date end,int k){
        return "";
        }
    }


