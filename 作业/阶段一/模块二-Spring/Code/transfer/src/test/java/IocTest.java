import com.springframework.context.ApplicationContext;
import com.springframework.context.support.ClassPathXmlApplicationContext;
import com.study.dao.AccountDao;
import com.study.service.TransferService;
import org.junit.Test;

public class IocTest {
    @Test
    public void testIoc() throws Exception {
        // 通过读取classpath下的xml文件来启动容器
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        TransferService transferService = (TransferService) applicationContext.getBean("transferService");
        transferService.transfer("6029621011000", "6029621011001", 100);
//        System.out.println(transferService.transfer("6029621011000"););

    }
}
