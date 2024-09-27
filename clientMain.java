public class ClientMain {
    public static void main(String[] args)  {
        try {
            GuiMain guiMain = new GuiMain();
            guiMain.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}