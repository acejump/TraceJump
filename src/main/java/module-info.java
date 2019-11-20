module tracejump {
    requires javafx.controls;
    requires com.google.common;
    requires org.bytedeco.tesseract.macosx.x86_64;
    requires jnativehook;
    requires java.logging;
    requires java.desktop;
    requires kotlin.stdlib;
    requires org.bytedeco.javacv;

    exports org.acejump.tracejump;
}