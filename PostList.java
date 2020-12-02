package com.example.plproject;

public class PostList {
    private PostNode head;

    public PostList() {

    }

    String getListData()
    {
        String data="";
        if(head==null)
        {

        }
        else
        {
            PostNode travelNode=head;
            while(travelNode.getNext()!=null)
            {
                data=data+travelNode.getData();
                travelNode=travelNode.getNext();
            }
            data=data+travelNode.getData();
        }

        return data;
    }
    String getListDataForDataBase()
    {
        String data="";
        if(head==null)
        {
            //list is empty
        }
        else
        {
            PostNode travelNode=head;
            while(travelNode.getNext()!=null)
            {
                data=data+travelNode.getData()+"/7/7/7/";
                travelNode=travelNode.getNext();
            }
            data=data+travelNode.getData();
        }

        return data;
    }

    void addNewPostNode(PostNode newPostNode)
    {
        int count=1;
        if(head==null)
        {
            head=newPostNode;
            newPostNode.setNumber(count);
        }
        else
        {
            PostNode travelNode=head;
            count++;
            while(travelNode.getNext()!=null)
            {
                travelNode=travelNode.getNext();
                count++;
            }
            travelNode.setNext(newPostNode);
            newPostNode.setNumber(count);
        }
    }
    void deletePostNode(int number)
    {
        if(head==null)
        {
            //postList empty
        }
        else
        {

            if(head.getNumber()==number)
            {
                head=head.getNext();
                updateNumbers();
            }
            else
            {
                int count=1;
                PostNode travelNode=head;
                while(travelNode.getNext()!=null)
                {
                    if(number==travelNode.getNext().getNumber())
                    {
                        travelNode.setNext(travelNode.getNext().getNext());
                    }
                   travelNode.setNumber(count);
                    count++;
                    if(travelNode.getNext()!=null)
                    travelNode=travelNode.getNext();
                }
                travelNode.setNumber(count);
            }

        }
    }
    void updateNumbers()
    {
        if(head==null)
        {
            //
        }
        else
        {
            int count=1;
            PostNode travelNode=head;
            while(travelNode.getNext()!=null)
            {

                    travelNode.setNumber(count);
                    travelNode=travelNode.getNext();
                count++;
            }
            travelNode.setNumber(count);
        }
    }
 /*   public PostNode getHead() {
        return head;
    }

    public void setHead(PostNode head) {
        this.head = head;
    }*/
    public void parsePostToPostNodes(String data)
    {
        if(!data.equals(""))
    {
        String subStrs[]=data.split("/7/7/7/");
        for(int i=0;i<subStrs.length;i++)
        {
            PostNode newNode=new PostNode(subStrs[i]);
            addNewPostNode(newNode);
        }
    }


    }
    public void deletePostList()
    {
        head=null;
    }
}
