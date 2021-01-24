
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class BaglanmaProjeV7 {
        
        private String kullanıcı_adı ="root";
        
        private String parola = "";
        
        private String db_isim = "omar11";
        
        private String host = "localhost";
        
        private int port = 3306;
        
        private Connection con = null ;
        
        private Statement state = null ;

        private PreparedStatement preparedStatement = null;
        /*
        
        Kaynak Mustafa Murat Coskun Udemy Java Eğitim İçeriğinden yararlanılmıştır.
        
        Database Transaction İşlemleri 
        Çoğu zaman programlarımızda bir çok veritabanı işlemlerini ard arda yaparız.
        Örneğin elimizde birbiriyle bağlantılı 3 tane tablo Güncelleme işlemimiz var(delete,update,)
        Bu işlemleri ve sorguları ard arda yçalıştırdığımızı düşünelim.
        
        statement.executeUpdate(sorgu1);
        statement.executeUpdate(sorgu2); // Burada herhangi bir hata oldu ve programımız sona erdi.
        statement.executeUpdate(sorgu3);
    
    
    Böyle bir durumda 2.sorgumuzda herhangi bir hata oluyor. 
    Ancak 1.sorguda hata olmadığı için bu sorgumuz veritabanımızı güncelledi.
    Ancak bu sorgular birbiriyle bağlantılı ise 1.sorgunun da çalışmaması gerekiyor.
    İşte biz böyle durumların önüne geçmek için Transactionları kullanıyoruz.
    
    (ATM Örneği)
    
    Transaction mantığını kullanmak için bu sorguların sadece hiçbir sorun oluşmadığında 
    toplu çalışmasını istiyoruz. 
    
    Java bu sorguları yazdığımız andan itibaren otomatik olarak sorguları sorgusuz sualsiz 
    çalıştırır. Onun için ilk olarak con.setAutoCommit(false) şeklinde bir şey yaparak bu durumu 
    engelliyoruz.
    
    commit() : Bütün sorguları çalıştır. Sorun olmadığı zaman hepsini çalıştırmak için kullanıyoruz.
    rollback(): Bütün sorguları iptal et. Sorun olduğu zaman hiçbirini çalıştırmamak için kullanıyoruz.
    
    
    Yani bu sefer programlarımızı biraz daha güvenli yazmış oluyoruz.
    
    Not : setAutoCommit(false) yazsak bile Veritabanını güncellemeyen bir sorgumuz varsa,
    herhangi bir güvenlik sıkıntı olmayacağından çalıştırılır.
        */
        public BaglanmaProjeV7(){
            String url = "jdbc:mysql://"+host+":"+port+"/"+db_isim+ "?useUnicode=true&characterEncoding=utf8";
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                System.out.println("Sunucuya bağlanma işlemi başarılı");
            } catch (ClassNotFoundException ex) {
                System.out.println("Sunucu Bulunamadı");
            }
            try {
                con = DriverManager.getConnection(url,kullanıcı_adı,parola);
                System.out.println("SQL TERİ TABANINA BAĞLANMA İŞLEMİ BAŞARILI");
            } catch (SQLException ex) {
                System.out.println("SQL BAĞLANMA HATASI");
               
            }
        }
        public void calısanlarıGetir(){
            String sorgu ="Select * From calisanlar";

            try {
                state = con.createStatement();
                ResultSet res = state.executeQuery(sorgu);
                while(res.next()){
                int id = res.getInt("id");
                String ad = res.getString("ad");
                String soyad = res.getString("soyad");
                String email = res.getString("email");
                System.out.println("/-Ad : "+ad+" /-Soyad : "+soyad+" /-Email : "+email+" /-Id : "+id);
                }
            } catch (SQLException ex) {
                Logger.getLogger(BaglanmaProjeV7.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
   
         public void preparedCalısanGetir(int id){
                String Sorgu = "Select * From calisanlar where id > ? and ad like ?";
                
            try {
                preparedStatement = con.prepareStatement(Sorgu);
                preparedStatement.setInt(1, id);//1. parametre id
                preparedStatement.setString(2, "h%");
                ResultSet res = preparedStatement.executeQuery();
                while(res.next()){
                    String ad = res.getString("ad");
                    String soyad = res.getString("soyad");
                    String email = res.getString("email");
                    
                    System.out.println("AD : "+ad+" SOYAD : "+soyad+" EMAİL : "+email);
                }
            } catch (SQLException ex) {
                Logger.getLogger(BaglanmaProjeV7.class.getName()).log(Level.SEVERE, null, ex);
            }
   
         }
         public void commitcerollback(){
             Scanner scn = new Scanner(System.in);
             
            try {
                //new area
                //bu aladna javaya sen bu işlemleri otomatik yapma ben bunları kendim yapacağım deme gerekiyor.
                con.setAutoCommit(false);//otokontrol mekanizmasını devredışı bıraktım
                
                String sorgu1 = "Delete From calisanlar where id = 4";
                
                String sorgu2 = "Update calisanlar set email ='hllbrisconnected@prince' where id = 4";
                
                System.out.println("Güncellenmeden önce ");
                calısanlarıGetir();
                //metod içerisinde metod çağrılmasına bir örnektir buradaki yapımız .
                //autocommit false olsa bile bu alan çalışacaktır.
                
                Statement stt = con.createStatement();
                
                stt.executeUpdate(sorgu1);
                stt.executeUpdate(sorgu2);
                //setautocommit false yapmasaydım bu alan otomaik olarak çalıştırılacaktı.Ancak bu yapıyı false yaptığım için şimdi istediğim zaman çalıştırabilecek pozisyona getirdim
                //Bunu yapmak için kullanıcıdan bir veri alıp verinin değerine göre işleme işlemini gerçekleştirebilirim
                System.out.println("İşlemleriniz Kaydedilsin mi ?(yes/no) ");
                String cevap = scn.nextLine();
                //cevap yes olursa commit olmazsa yapmamış olacağım
                if(cevap.equals("yes")){
                    //kullanıcı onay verirse benim bu iki sorgumu çalıştırmam gerekiyor.
                    con.commit();//buradaki komut sayesinde işlemlerimi yapmış olucam
                    calısanlarıGetir();
                    System.out.println("VeriTabanınız Güncellendi...!!!!");
                }else{
                    //burada hiçbir işlem--commit yapılmayacak
                    con.rollback();//bu yapı sayesinde veri tabanım güncellenmemiş olacak
                    System.out.println("İsteğiniz üzerinde veriTabanı üzerindeki değişiklikler iptal edildi. ");
                    calısanlarıGetir();
                }
            } catch (SQLException ex) {
                Logger.getLogger(BaglanmaProjeV7.class.getName()).log(Level.SEVERE, null, ex);
            }
         }
    public static void main(String[] args) {
    BaglanmaProjeV7 v7 = new BaglanmaProjeV7();
    System.out.println("***************************************************V4");
    v7.commitcerollback();
    }
    
}
