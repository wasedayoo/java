import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;


class Sctos extends Thread{ 
   private boolean end = false;
   private BufferedReader in;
   private PrintWriter out[];
   private int sw;
   private int max;
   String str;

   synchronized boolean end() {
       return end;
   }

   public void set(BufferedReader i,PrintWriter o[], int s, int m){
       in=i;
       sw = s;
       out=o;
       max = m;
   }

   public void run(){
       try{
           while (true) {
               try{
                   str = in.readLine();
               }catch(IOException e){
                   str = "nul";
               }
               if (str.equals("END"))break;
               if (str == "nul") continue;
               System.out.print("client"+sw+": ");
               System.out.println(str);
               int j=0;
               while(j<=max){
                    if(j!=sw) out[j].println("client"+sw+": "+str);
                    j++;
               }
           }
       }finally{
           System.out.println("Client END");
           end = true;
       }
   }
}

class Sstoc extends Thread{ 
   private boolean end = false;
   private PrintWriter out[];
   private int sw;
   private int max;
   Scanner scanner = new Scanner(System.in);

   synchronized boolean end() {
       return end;
   }

   public void set(PrintWriter o[], int s, int m){
       out=o;
       sw = s;
       max = m;
   }

   public void run(){
       try{
           while (true) {
                String input = scanner.nextLine();
                int j=0;
                while(j<=max){
                    out[j].println("server: "+input);
                    j++;
                }
               if(input.equals("END")) break;
           }
       }finally{
           System.out.println("Server END");
           end = true;
       }
   }
}

class f extends Thread{
    private boolean end = false;
    Scanner scanner = new Scanner(System.in);
    private Socket socket[];
    private ServerSocket s;
    private int i=0;

   synchronized boolean end() {
       return end;
   }

   public void set(ServerSocket ser ,Socket[] so){
    s = ser;
    socket = so;

   }

   public int max(){
    return i;
   }

   public void run(){
       try{
            System.out.println("connect start");
            while(true){
                try{
                socket[i] = s.accept();
                System.out.println("Connection accepted"+i+": \n" + socket[i]);
                i++;
                }catch (IOException e) {
                    System.out.println("An IO exception occurred: " + e.getMessage());
                }
                // String input = scanner.nextLine();
                // if(input.equals("NEXT")) 
                // System.out.println("connect end");
                // end = true;
                // break;
            
        
              }
            // while (true) {
            //     String input = scanner.nextLine();
            //     if(input.equals("END")) 
            //     System.out.println("connect end");
            //     break;
            // }
       }finally{
           //end = true;
           System.out.println("connect end2");
           //System.out.println("Connection:"+ end());
       }
   }
}

public class JabberServer {
//public static final int PORT = 8080; 
 public static void main(String[] args)
 throws IOException {
   Scanner scanner = new Scanner(System.in);
   int PORT = Integer.parseInt(args[0]);
   ServerSocket s = new ServerSocket(PORT); 
   System.out.println("Started: " + s);
   try {
    //   Socket socket1 = s.accept();
    //   System.out.println("Connection accepted1: " + socket1);
    //   Socket socket2 = s.accept();
    //   System.out.println("Connection accepted2: " + socket2);
      //Socket socket[];
      int maxclients = 10;
      Socket[] socket = new Socket[maxclients];
      f connection = new f();
      connection.set(s,socket);
      connection.start();
      int i=0;

      while(true){
        String input = scanner.nextLine();
        if(input.equals("NEXT")) break;
      }

      System.out.println(connection.max());
      i = connection.max();
      i--;
      System.out.println("check");
      int j=0;
      BufferedReader[] in = new BufferedReader[maxclients];
      PrintWriter[] out = new PrintWriter[maxclients];

      while(j<=i){
        
        in[j] =new BufferedReader(new InputStreamReader(socket[j].getInputStream())); 
        out[j] =new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket[j].getOutputStream())), true);
        j++;
      }
    j=0;
    //   BufferedReader in1 =new BufferedReader(new InputStreamReader(socket1.getInputStream())); 
    //   PrintWriter out1 =new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket1.getOutputStream())), true);
    //   BufferedReader in2 =new BufferedReader(new InputStreamReader(socket2.getInputStream())); 
    //   PrintWriter out2 =new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket2.getOutputStream())), true);
    //Sstoc servercomment = new Sstoc[maxclients];
      Sstoc servercomment = new Sstoc();
    Sctos[] clientcomment = new Sctos[maxclients];
    while(j<=i){
        //servercomment[j] = new Sstoc();
        clientcomment[j] = new Sctos();
      j++;
    }
    //   Sstoc servercomment1 = new Sstoc();
    //   Sctos clientcomment1 = new Sctos();
    servercomment.set(out,i,i);
    servercomment.start();
    j=0;
    while(j<=i){
        System.out.println("checkstart"+ j);
        //servercomment[j].set(out,j,i);
        clientcomment[j].set(in[j],out,j,i);
        clientcomment[j].start();
        //servercomment[j].start();
        j++;
    }
    System.out.println("checkstartend");
    //   servercomment1.set(out1,out2,1);
    //   clientcomment1.set(in1,out1,out2,1);
    //   clientcomment1.start();
    //   servercomment1.start();
        boolean[] check = new boolean[i+1];
        Arrays.fill(check, false);
        boolean alltrue = false;
      while(true){
        j=0;
        while(j<=i){
            if(servercomment.end()||clientcomment[j].end()){
                socket[j].close();
                check[j]=true;
                break;
            }
            j++;
        }
        for (boolean flag : check) {
            if (flag) {
                alltrue = true;
                break;
            }
        }
        if(alltrue) break;
      }

      System.out.println("completely END...");
    
   
   } finally {
      s.close();
      System.exit(0);
   }
 }
}