/*
 * The MIT License
 *
 * Copyright 2019 francescoandreuzzi.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


package it.andreuzzi.comparestring2.test;

import it.andreuzzi.comparestring2.AlgMap.Alg;

/**
 *
 * @author francescoandreuzzi
 */
public class TestResult {
    String s1, s2;
    
    Alg alg;
    
    double result;
    long time;
    
    public TestResult(Alg alg, String s1, String s2, double result, long time) {
        this.alg = alg;
        
        this.s1 = s1;
        this.s2 = s2;
        
        this.result = result;
        this.time = time;
    }
    
    @Override
    public String toString() {
        String algName = alg.label();
        while(algName.length() < 15) algName = algName + " ";
        
        String type = String.format("(%s)", alg.category());
        while(type.length() < 25) type = type + " ";
        
        String r = (result != Float.MIN_VALUE && result != Float.MAX_VALUE) ? String.format("%f", result) : "n/a";
        while(r.length() < 15) r = r + " ";
        
        String what = String.format("(%s - %s)", s1, s2);
        while(what.length() < 30) what = what + " ";
        
        return String.format("%s %s : %s --> %s [in %dns]", algName, type, what, r, time);
    }
}
