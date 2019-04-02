package ptest;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

public class performanceTest implements JavaSamplerClient {
	private SampleResult results;
	private String a;
	private String b;
	private String resultData;
	
	//���ô���Ĳ������������ö��,�����õĲ�������ʾ��Jmeter�Ĳ����б���
	@Override
	public Arguments getDefaultParameters() {
		Arguments params = new Arguments();
		params.addArgument("a", "0");          //���ò�����������Ĭ��ֵ0
		params.addArgument("b", "0");          //���ò�����������Ĭ��ֵ0
		
		return params;
	}
	
	//ִ�е�ִ���壬�����߳�����ѭ�������Ĳ�ͬ��ִ�ж��
	@Override
	public SampleResult runTest(JavaSamplerContext arg0) {
		a = arg0.getParameter("a"); // ��ȡ��Jmeter�����õĲ���
		b = arg0.getParameter("b");

		try {
			results.sampleStart(); // ��ʼͳ����Ӧʱ����
			outputServer test = new outputServer();

			// ͨ������Ĳ����Ϳ��Խ����ⷽ������Ӧ�����Jmeter�Ĳ쿴������е���Ӧ���������ˡ�
			resultData = String.valueOf(test.output(Integer.parseInt(a), Integer.parseInt(b)));
			if (resultData != null && resultData.length() > 0) {
				results.setResponseData("����ǣ�" + resultData, null);
				results.setDataType(SampleResult.TEXT);
			
			
			}
			System.out.println(resultData);
			
			results.setSuccessful(true);
		} catch (Exception e) {
			results.setSuccessful(false);
			e.printStackTrace();
		} finally {
			results.sampleEnd(); // ����ͳ����Ӧʱ����
		}

		return results;
	}

	//��ʼ��������ʵ������ʱÿ���߳̽�ִ��һ�Σ��ڲ��Է���ǰִ��
	@Override
	public void setupTest(JavaSamplerContext arg0) {
		results = new SampleResult();
	}

	//����������ʵ������ʱÿ���߳̽�ִ��һ�Σ��ڲ��Է���������ִ��
	@Override
	public void teardownTest(JavaSamplerContext arg0) {
		// TODO Auto-generated method stub
	}
	
/*  public static void main(String[] args) {
		Arguments params = new Arguments();
		params.addArgument("a", "1");          //���ò�����������Ĭ��ֵ0
		params.addArgument("b", "2");          //���ò�����������Ĭ��ֵ0
		JavaSamplerContext arg0 = new JavaSamplerContext(params);
		performanceTest test = new performanceTest();
		test.setupTest(arg0);
		test.runTest(arg0);
		test.teardownTest(arg0);	
	}*/
}
