import com.github.b402.cmc.core.FileManager;
import com.github.b402.cmc.core.module.ModuleClassLoader;
import com.github.b402.cmc.core.module.ModuleLoader;

import java.io.File;

public class Test {
    @org.junit.Test
    public void onTest(){
        FileManager.INSTANCE.checkFolder();
        File modules = new File(FileManager.INSTANCE.getBaseFolder(),"module");
        ModuleLoader ml = new ModuleLoader(this.getClass().getClassLoader());
        for (File file : modules.listFiles()) {
            ml.load(file);
        }
        for (ModuleClassLoader moduleClassLoader : ml.getLoaders()) {
            moduleClassLoader.getModule().onEnable();
        }
    }
}
