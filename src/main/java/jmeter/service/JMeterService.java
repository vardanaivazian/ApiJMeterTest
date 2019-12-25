package jmeter.service;

import jmeter.bean.AssertionBean;
import org.apache.jmeter.assertions.ResponseAssertion;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jorphan.collections.HashTree;

public interface JMeterService {
    StandardJMeterEngine engine();
    void run(StandardJMeterEngine engine, HashTree testPlanTree);
    LoopController loop(int loopCount, boolean first);
    ThreadGroup threadGroup(String name, int numThreads, int rumpUp);
    TestPlan testPlan(String name);
    HashTree testPlanTree();
    HTTPSamplerProxy httpSamplerProxy();
    void saveTree( HashTree tree, String destination );
    Summariser summer();
    ResultCollector resultCollector( Summariser summer, String reportFile);
    void addReport(HashTree testPlanTree, Summariser summer, String reportFile);
    void generateDashboardReport(String source, String destination);
    ResponseAssertion responseAssertion( AssertionBean assertionsBean );
}
