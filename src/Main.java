import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        // public parameters for bilinear pairings
        Pairing pairing = PairingFactory.getPairing("a.properties");
        Element g = pairing.getG1().newRandomElement();
        Element h = pairing.getG1().newRandomElement();


        // generate a set S
        ArrayList<Element> S = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            S.add(pairing.getZr().newRandomElement());
        }


        // choose a secret element (sigma) from S
        Element sigma = S.get(0);


        // generate commitment for sigma
        Element r = pairing.getZr().newRandomElement();
        Element C = g.duplicate().powZn(sigma).mul(h.duplicate().powZn(r));


        // Prover picks x and sends y and Ai for every i in S
        Element x = pairing.getZr().newRandomElement();
        Element y = g.duplicate().powZn(x);

        ArrayList<Element> A = new ArrayList<>();
        for (Element i : S) {
            Element element1 = pairing.getZr().newElement(1);
            A.add(g.duplicate().powZn(element1.div(x.duplicate().add(i))));
        }


        // Prover picks v and sends V
        Element v = pairing.getZr().newRandomElement();
        Element V = A.get(0).duplicate().powZn(v);


        // Prover picks s, t, m and sends a and D
        Element s = pairing.getZr().newRandomElement();
        Element t = pairing.getZr().newRandomElement();
        Element m = pairing.getZr().newRandomElement();

        Element a = pairing.pairing(V.duplicate(), g.duplicate()).powZn(pairing.getZr().newElement(0).sub(s));
        a.mul(pairing.pairing(g.duplicate(), g.duplicate()).powZn(t));

        Element D = g.duplicate().powZn(s).mul(h.duplicate().powZn(m));


        // Verifier sends a random challenge c
        Element c = pairing.getZr().newRandomElement();


        // Prover send zdelta, ztau, zgamma
        Element zdelta = s.duplicate().sub(sigma.duplicate().mul(c));
        Element ztau = t.duplicate().sub(v.duplicate().mul(c));
        Element zgamma = m.duplicate().sub(r.duplicate().mul(c));


        // Verifier checks that D=res1 and that a=res2
        System.out.println("D = " + D);
        Element res = C.duplicate().powZn(c).mul(h.duplicate().powZn(zgamma)).mul(g.duplicate().powZn(zdelta));
        System.out.println("res = " + res);
        System.out.println("D.equals(res) = " + D.equals(res));


        System.out.println("a = " + a);
        Element res2 = pairing.pairing(V.duplicate(), y.duplicate()).powZn(c);
        res2.mul(pairing.pairing(V.duplicate(), g.duplicate()).powZn(pairing.getZr().newElement(0).sub(zdelta)));
        res2.mul(pairing.pairing(g.duplicate(), g.duplicate()).powZn(ztau));
        System.out.println("res2 = " + res2);
        System.out.println("a.equals(res2) = " + a.equals(res2));
    }
}