module tracejump {
    requires javafx.controls;
    requires com.google.common;
    requires jnativehook;
    requires java.logging;
    requires java.desktop;
    requires kotlin.stdlib;
    requires org.bytedeco.javacv;
    requires org.bytedeco.tesseract.platform;
    requires org.bytedeco.leptonica.platform;

    exports org.acejump.tracejump;
}