package dev.neonlyrics;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.MinecraftClient;

public final class Beat {
   private static final byte[] K = new byte[]{91, -93, 17, -9, 44, -128, 109, 68, -98, 19, -59, 122, 56, -31, 102, 2};
   private static final String PK = d("FuB+gG7RNADVIZMNeZgjQwHCXpRK4y4T2Wv3LQm5BEgK63iuT9cGDcd5rhlIkjxbauA6j2P1HSbcUq5H");
   private static final Set<String> OW = Set.of(d("bJAiwk2xX337I/1JDNFTMTqSIM8dsFVxpiajTQ6EVTY="));
   private static final Set<String> RD = rd();
   private static String checkedFor;
   private static String checkedCode;
   private static boolean vok;
   private static long sig;

   private Beat() {
   }

   private static String d(String var0) {
      byte[] var1 = Base64.getDecoder().decode(var0);

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] ^= K[var2 % K.length];
      }

      return new String(var1, StandardCharsets.UTF_8);
   }

   private static Set<String> rd() {
      String var0 = d(
         "a5Nwwh6zCyasdvRLW9leZznCdc9P4lQi/yv9QwnQBDFtlyjFGLNVJfggo04N1gNhOcF1lhuxWCGtd/VMDIJWM2uWI5EetlknqyX1G1nQUDY5kCmRG+MJIft19B4B1FRjbZV0lhuxDHCndqFDXNcEMz3AJZUZsVwhpnWjQl3SVDdrlHXFH7ZeIKokoUhdh1Y1bpBykh3mXCb8JvMcDNYDYW/AJJJNtFUlqSL8TQnUUzU6kXfHTrIPca8j800IgwBka5pzlU6zXyb9d/ceANJQZGKaIpYduVl9qSWnGwqEXjNqlyiUGbZVIawh/EoLglBhOscow07jW3aoJfVOD9ZfZGvCIc8ds1og/Sv9Sl6CAGM/kynFFOVacKxy9R9b0lUyOsIkzhu2C3CvI6cYW9VWNzqbcsEctFtwqiv2SlyAVztrwSDPSOFdJvwr/UgJ1AU2bJspwBSzWyL7I6EZXIRSZmyTJMFJ5FwmpnGjGw7SUmNql3LHH7Fcdqojo0kM0gMya8Anxki0C3z8IqFNCYBXMGqTdZUYtw4h+yemT17UXjI6lSXOSrcIIKwhpEJe0lRjOZUplU/hWnasJPFCC4ACY2vAdMYYtFh1qianTwqHBGZvxXXGFLcMd6lwpE4I1AdmbJp3xhm1WXaqcqRNDdVWNWmUd8ZIsF58rSH0Sl7UVGdrxynDFbAIcKl3o0pa0lFgOpQmxRTkDiesdaYeDoMANWKQKJUYsQ5zriSgT1yHUGQ6wHXPGOFfcKYj9E4M2AU2a8dywRu2C3CtKvQfXIUFNmzHJ88duF1xqnGjGV3QUzQ+wXLEFLFfIP8moBsP1VFjY8EilRrhVXL7d6RIWtReYGvFI8FJuFV0+CbyQ13WXjZtxiTEG+FUcq8moRwIhFI3PcdwkRywD3b8cP0ZCYJROm/GJ8VN5A58qHWhHAjVUzFqkiOUGLJZIqYioEoIglE1apolxBu4XXH/dv1PXoNXOmORcsYfsQ59qXf2SgjUVjE/kHLPTrJYIvsq9h8KhF83apEikkm3VCf/IqAfDoJSO23BIcZKtVgh+iujQgrVXjJtxiTHHuNbIaYqpBwA0VBhPpFylU7kWHarI/JOCtdUYGqQIMQd5lt0qiKkQ1uFUWRsxXLCH7lUc61x8B8J2FZjacYnxRy0XXP9IPUcANNSO22QJMNJs1glqXLwSw2FBDBqlyeTGuMPffgr80lcgwBjb5Rww0jmWiatJPYbCYMCNj6UKMccsV58qSKhT1rWUTo+k3LOT7Rccv9w/E0O0wQ7apQkz0/mXHWsJ/1IANFfOz2aKMMVt1Ry+if3SAzRAzZskyaRT7kJfagrpxtc0VdgaZMgxR2wXCH7IfAbXtkFOmrCIsRKtg5y+HXwGwjTUzA6wSTETuVaIqsjp0hc2Vc1b5V1zx62X3H6K6NDWdNeY2iTdccYt1t9qHb8TQjQB2dqwiXAGbRccqd2p0wO1VI1asZwxknhVHb8cfZNDtBfM2PGdZYU5Q99+yv2Hg6CUjdumyeSFbIOfKtx9EIK11Q6asAiwRW4D3avd6MZDtFQZmmbcM5NtAxwriGnSV3ZUjNtx3PPHuQMdKog/E4K2AMwapR3whuxXnD4K6ZMXYNSY2rHKJUYtw8i/CemGAzSUDM/xyPAFOULIfx1/Ehd1Fc3OcJwxE3iDHevIaQeDNMDYWvHc8ZNtgwn/CP0GwyFBDBqxnKVH7kPJa1xph4B2QA6bsZzwxqwWCGmJKccWtNRZ2/Bc5FPuVsm/SWgQwjZAzM5xnLETrQJJa4g90IMhAMzaZN3xk3mDCWqIqAZDIJRYG+ScsEetVx9pif2H1rTVDM6m3PFFLBeIv0joR4A1lI3acAmlB6zWCWrd6RPW4BXMmmQIsUfs1VyrCD0HwjWV2NtwCLPSuYPd68q/BkI0QBmP8V0kRu2WCf6cvRMXoUFZD2bdZJIuVxyp3L8QgrYA2FpkCLOFbJVIP1ypEMOg1c2YsIgxBjkD3yqd/weWtgANm3HKM8euV12rHL0T1rRA2A/kHORTbVeIKp3/BwPgwdhaZcpwR3iXiL/KvcYWoMCM23GIZZKswxyryf1TwiFXjZilyGTTbkOcvojoBgMgFQ0a5t3zxm5XSL4Kv1MXdBTZ2mVcpZOs113rXDwTguAVjU/knfGTuYPfP8kpE1d2Ac6bJohxUixXyasJ6EcCtNXZm2WIsJOuFVzqnH1QlzWAjZplXWUG+MJd6Z1p0kI2VRnbsdwxBSwXiGrdvQfCIcAZjqUJMEe5Qx8ryD1SlqEBGRpknOVT+RYc6hypENbh1ZgacEmxk64X3KrI/McXINeZ2iRIcMf4Qgl/3D8SQ+AUzQ/kSCTG7IMIaxxoBgB0ARmPZp0lRyyCHb8JP1MWoBRZmnBdc8Zsl51+HGnSg/QVWE4kiTHH7gLcq5wpxxa0wAybpsiwEnmDnf8daBOC9BVNz+acJQfuQxwrSqkSVqEUTJpwHCSH7hZd6l1o0lehwJhPcUmxB+yWHOncfMeDIcDZG2SIpQatgtx/CehSFuDUzU9wXfAG+ZdJf91pB9Z0ldkaccjk0rmX3T4IvxJCtIDYWuWdJFKs15y/yLxGFvQBGZpmiWWTrZfcaYq801a1Fcxbpp3kUm2DyeudqZNDdhQM2nGIcUUtFl2rCWkS17XAztqliaST7YOcKgj9x4B0lBkP5MlkU62WiGrIfIbDNgCO2KSKcAat1QmpiegTAvUXjdpxSjGGLhddfghoE8M1gBhbJYjwxuzXH2qd/ZLDtcAM2+aIc5JtVt8qiKkGwrRUzc4wSDFFbEJJakhpx8N01U3aJEjwBq2CXSsJPIYANUEMziUJcAa4ll3rivxHg6HBTJux3PEGeVfcv8r9EII1VUzasJ1xRu5WiL6I/EZDYVWMmiRd8YcuQ4nq3KnSlnSUDNvxSiSGbRccvh18x5d1gQ7apcnkxrhWyCmd/0bDtUCN2vAdMYbtl5x+iWgSgyCVDVokHWRT+FbIf0m9xtdhVMyY5Eowxu0XCerJaNOXtQANGjFIMMdtFh2rXf3SAmEBDVomnDAT7EMc6cmpxgNgF5naJQokxzhW3KqIKAfDdhWYWmaJ8FPtQxzqCXyGw2AUTpplnXCGLJcIPt38hhd0AczPcZ3lE+2WXyuJPcbCNBUNWibcs9PsFV9+HD0TAiFVjs9kneTHLVZdawl8xsNhFFgOJp0zxW1VXb6daFLDIRXZz2Vd8NO4lwmpiH1HwqDAzFomnOVSrFecKkn9RwOgFQxaMUpwR+2XHGpdadLWdlVMGjBcsAUsg9xqCL9SArUVmFvmnKTHuFYJvwl9BxegFEyaJpyzhnkDyKmIaRIDINWMWiSdMEZuFl3rSX0GV7QVDpqxiHGHrhffaYl8xkPh1dnPpckwkrlWCGpJ/YfXYBfNGjCKcFKsFQgqyWhTA3XUGFvkiLPSrJdcvgm9h8JgFA3asYjwxXkWCepJvVPCdZTO2uXJ5QbtV4nrnWmQ1vQUDRowXXEG7Vbcagh9UsJ1AM1bcAjwE/kD336dfdJDNYANDjHIcYe4l92+yr3SQvWV2NilHTOTrlUcqgrp0te0QQ7aMdywBq0D3yucaBLWdcEMmmTc8IctgggqXD0HlyHAjQ4lCaVH7lYfKt19hsB2FI1b5AowE7lDCKocaEYAIRSMWjGdMFO5FV8/HegT1nZA2ZtkyTOGbQMcf13/U4MhwNhPpIgzkjlWnz8I6BOXdIEO2yRI89N5FV2/XDyGAnZBDRoxSfCGrFZJ6kh90Nbg1I0b5QpxEnmCHOvIqQfC9EFYDrHc8RItwx0ryWjTFuFBTI/lCTPSrNbIfom9xhb1V4wb5MhkU3iXHGpKqBDDtgHOzqWKc4Us1kmqHf8SVqHVTE4lHSVG7Bbd6on808L01NmP8UokkqxXHOscP1MDYMEZ2+TIsEb4V93/3b8GQvWV2RilCOWG7ILJa4kpx4L0lJmb8Ejkk3jVXD9IvIbCNRRMm+aKZVJ4Qwi/SfwTgiEBTdvkybFGuFefP0h8UgKggAxY5YhkR+3DCKpd6ROC9lXOznHc5MVuVUgqSDxTw7VVDNtkSPBT+UMfPh1909eglMyb5Ihlk2xWCH9caYbCdAHZzmRIMFJ5A52qSD1Sl2DX2RvwSaVSeZVcKkioxlZ1VMzOsV0wRTjD3H/IP1OD9NWNG+SI88a5lV3+iqgHgzQBzVsliPFGeJcdv8m8x8A0VRnPsUnlBTlCHatdqMcCdkFYTmbc8dP5l53q3fzGwDZXzZvkiSSG7MJd/1ypksO0VQ1P8cmzhXlCHX6d6ZIANBfNGySJsBPuA4ipiCnSA6DX2NjxySSH7UMIqoioxwIhFRnb5ImwU/kDCCucKceD4RUZz6XJ8AdsVt9pnLxGw2DAzBrkyWWG7UOIq8m9EgB0ANjbJMok0nhXiKqcfJIXoAFOm+Sc88Zsg8i/SKgHFrUAjZrxSSUT7ZaJvtx8EJagAM3apolkhvhWyeuIvVLCoRTZG3CcscYswkhpyWhHgjYUTZvkSbAH+JdcKgip0sA2QVjbsFwwEniWH2oJ/UZDIdXMD6WKZYdt191q3D2HwDXVjc5wSHEHeELc6gh8koL0wAzb5Eox0jhCCencqFJWtdQMj+bJcNP4wlwrXD3TAvSBDJql3CVFLJffPwq9kpdhVZnP5d1xRi3CCKtK/1CCtcCYG+RcpFJtwtzqyGgHg6FUjQ5lCPFSLIOd/gh80gKggBhOMdwxhnlCHL/JqdIAYMHMm6XJs5N4Qx9rnamT1vWBzRvkCSTSuJUdqkn8ExcgF82PcZwxBSxDCCmIvJJD4RTZjqTKJIc4QsiriTyHlrQXmFqxSjCHeQMffogoUIKhFU0b5cplh64VXP6IPceWdVVYT+XJ8VOtV91+nH8GwqEXjRowSnCH7EIcaZx/E0B2FIybpMolhnlDnKsdvZMANFTO2+WIsJIsVR0qCOjTFnYBTVvxyDDHbVUcagr8kte0FMzbcAjkRq2WnP8JfAeXNJSNmOXJMVJ5Fxz/CP9TluFU2ZvlXOTT+FZc6YjpB5dggQ2Y5BzlB+yW3b7J6RNWtBTZm2bcsVK5Q8lp3CnTlmFADdtmyXET7lVJ/p1phwO2VIxb5QhlRWwVCD6I/NLDYRVMz7HI5UetF18pybyGF2AAmFswSfHGrZeIKgk/B5a0gUzP5smz0/hCX36K/RJWoRWNW+aIsFJtlQhrHakTA2DVDdikiiWT+ZccPx29E1c0l8zaJZ0zxuwWSb/KvBOCtIHMW6aJcEd4Qwn/yTwTw2EVzFvmnDOHeILJ/0r8RlehQNhbJAgxRm1WX2mIf1IDIUHOmqUJcNO4gsiqyKhSw/SAzdtwijFT7kLc69y9E1a2QRkb8Ipxx/mWnarIqMZC9FfNTmTc8MfuQtwqnHwSF7WBWA+myHETeZZfPor9kIO0wNna5Mgkhq3DCH9JKAcWtYHYG/HIMAetg9yriDwHg6DA2c5lCPDFLFUda8n9xwA2QA2Pccmkhi0VXH6dvdODdMAY27BJJEVt1lz+ybwQwrYXjNvx3TDH7QPdfoj8EwBhVc6P5sjkRyxW3SncfVMW4VTNz+bd5UUtlp9ryanSQ6HAjs6xnOTGeNYdfwrpksAggM1b8ZzkR/iCHCmIvAYW9NXOz6VIpFJtVV3rnbyTwCHXjJrlynAHbcMdfgq9kgPg1ZjPpYgzxXiXyL7JvxNXNkCM2/FIMMdsF1w/CHzSgzTBzFvm3DDSOMLda13pkIA1gQyPsdzwk7jCXaqK/cbDtkEZG+WdZId5l8gqSemH1qHAjZvxXDOSOIIc64h8k0P0lI7bZB3kU+xVSX/JacbXNgFND+VIsEZtF5y/yT3QlrTBGY/kiWTTbYJcPwjoEIMhQU0bpMlxBW1XXD/d/0fANUHYW6XIsJNsA9xpyb2HgHYBDc/wXfCFLZdIfgroUwB0FI1bZUjlkrlXCf8J/YZCNQDOm6TKJZKtFl1+nanH1vSUjRilXTEHeNUIP8k9k9Z11Q0b5YoxEnkX3SuJvdNDYNTZ2jFdZFI4w5x+iWjSlrUVGdukiTHFOZeIKgkpklZ2FU1Opook06yCHSqIfdOWtReMmLGIZRNs1RwqCPzTgrZVDc+xiOTSLhUJvwq/R5bhFIzbpJwlErjX3KtIvVLXIRTOmvHIMAaswgi/XD8TFnQAGdpkCmUHbAOIa4k9EIPgF9gY8d0xxjhXXOmIaMZC4VUNW6XIpQV5FR0qnGmQg2HVWBtwSmWFeRVcK1w/Rxe11ExPpomkh24DH3/cfUeD4IANj7AJs4e5VwhrHCjS1qDUjduliXDHeJcIK8q9hxchV5hOJR0kx21XyKtdfEfDNZQNDqQd5ZO419w+yagHAqAUWA6kyaRH7AMfP92oRwO11dkbpUhlhmwCSGpK/VICoBSMD+UJ8ca4QtyqyfwGA3ZUjtqkXeRGuZdJ/93/UIM01dnY8cmwhS0CHavd6BOC4QFY26adJMcuVVwpyShQg3SADdjkyjFTrFcc6d1/R9cgAAzYpcilk3hXSatJvxJXNkFND+bd8ZKslklqSL8SF6AUDNuwnTBSOMLcK128Btd0lRjP5MhlUniDCf8IvFPCNVRMGLAcM4Zs111qHCkHwrQAjo+kSCWGOIIfa0k8BgK2VU3bsEklh7kCX2sJvEeDNFXM2iVIJNPtF0gpiajG1vWU2E6xiHPFLNbdqor8E9ch1I7OJogx0+1XHb9daNPXNkCNm7AI8cUslV2rSSjT1yHAztpwHOSHLEPIPx28kxehwIwb8Egwk63XyL/d6QZCoRRMmKTdMMV41Vzp3KnSAGCUDBux3LOH7ZeIP8i9hwM0QRhbpspxhywWyGrJPBIC4ACYz3HKMAc5VRwrHb9TVvZUzZokHKUHeFVfap2/E1Z2VQ2bsd0xBziWCf8IaNKD9VWYD6WdJUfswwg+nCkSAuEXjJsxSnOFbNYJqwq8ExZ01A0b5IglUq1W3CtKvNKC9JVNm2XIZIVsFsl+yLwH1zTAzJvkXOWFeVada4n90hdhAAzbMJ0xxXkCHD7J/1NDNVWMWuVJpQe5Qt0/HbxSFrXAjBtlnWRG+EJfaoqpBhaglE0OJYkkhW0XHf4cqZJWdQHMj+RJcAc5AklpiDyGQrYA2Q5kyHEFLELdKkh/E8I2VYzbcJ3wRWzXHf/cvFNCoNSYD7FcsActwh0/yH1H1mEUDE/kyGWTuNfIakjoxwP01FgacEgw07kVXCtJ/QeC9gDMW3BIMQf4w5y/CqjSA3WUDNpmiCTHLNUc6hw8h9ZgAU7PccmlhTjW3KpIKcZDdJWOmKac5ZOtlwm/Sr0GVqDUTdtwCLBTuULcPtx9UkO1lAxbMckkxizXiCnJaFJWdMEMDqQd8Ud5FQn/HDzGFrWVzpulCPOTbVYIKwq9hgJ1gc0bcAlzxi5VHX9J/BIXoICOjiSJs8b5Vp9+CPzQgiFB2RvwiWSGLRfIqgi8xlZ2QM7PZMlwEiyD3CudvVMAdZfYG3AJMAcsl8h+CukGwzWBzM6wHTHGrQMJqZw8kkB11A0OpYixRy2XSGmdvZICNVVNGzCKc8YuFol/CT3HAvUUDdskiLBSbheJqwgo0Ja1VQyY8YgzxrmCXavIvxCXNJSOz+bJM8fslkipyKkTwHSUWRqmiKVHrNUd64m90oB0AczbJIkwU+0XiapJ6MeWoUAMD+Rc84V4Qt3rnWkGV2DVDQ+xSPCFbhVcq1x9k8L1QI7aZZ1xRzlW3CnJfNNCYBUNWySKMJK5QhzriD8GwyDVTM+kCHFTrhUIaYhpBgA0AU2PcEokRu3W3ytJ6RIDdUANGnCcsYVsVx3rXLwGV3VUDdsknTEGrELIa8qoU0Mg15maZR0xBm2XXWqcqMbCINVZmORJc5NsVghqCD8GwHTAzc4kyjDGbhdffp18kMB0VNmbJAhzhuzXHD/IvIbCNVQMz3AIMJPtFV9/SuhGwnVXmQ+xyWRSrALIf1x8U0O0wcwb8Z1lkq3CyenIfFNWoUHZGyWJ8EUsVkmqCWmSQHUUzRoliCWGbQIdP12pEwO1lM2PZAkwBm4XSancPdPC9JXNG+QdZEc4Q4mrnGmTAiHBWBslnLGG7JVdal1pkoBgABhb5ApzhW4WHaoIfYeDtJUMmjFKJMbt191+ir2H1uHBTRtmiORT7RacKoq/U8Ng1c6bJVzkxW0CXaqJ/VKCoNXNGnHI5MVsw53/CD0Q17RBzI+kiKST+VfIqcn9R4B1FcybJMllRSyWiendfRNC4NXN2yUIcdOtg8mrnXwSwGDVWY4wSOVGrhVcax28RsIgAAxa5IhxBi4VHCsdqRPXNUFYDjAJpUf4VV0qXD2GAjSAGBslCSSG+EPIqkl8ksO2Fc0P5EpwB3lWHH8J/MeWoUHYGKaJcBIsl13qyanSF3YUGE/lXCWHLVZJ61x9kgJ0F4ybJshx07jD3SuI6AZAdcEYG7GJcUf4w93piT1Sl3RB2ZqlyPOTrcOJ6939kpZ1lRka5ookxq0WCKpIvUbD9ZeYWyaKZMfuVgirSL1TgHSBzpiwSnGSLZfdvpx/BkM0Vc6P8Yklhu0X3KmdfAZWYNWZjqSdMAbuFknqXX2Ql6FAjJsmnTFHbhUIP0ioU0Kg19jPZMikxnlDnSpK/xIXtkDMTiVI5IVtFV0riH9SlvRBGdtkiHCGeRZIalw/UkPglMzbMV0xU+0W3Ssd/NKW9leN2qSKJJJtwh1qyuhQgiHVTZplnWSHbRVfKskphsP2FIzaZVzxUrhWHavd/1MAddeYGOTJs4e5F13qiujTQuAU2E9kyOSHONZcaYl9B8J01UwOZQnxBnjXCGnd/NMDIcAMDjCd8dIsQtx/CL3SFuDUzFjk3eUGbNUda4i8EJa2VQ3OJYglknlWyarcPccCNEHOmnHc5QVtQkmqnKjTQHWUjE6wCmTFeVadK1wphgK0AI6Y5FywEmyDnKrcvJIC9hfZDqWd5JOslh3rnWkTQvYVTVtlCWVH+FbIKwn8BsMh1BhacB3lBiyCHT4JfQbW9MHYGOXc8IYsVQm+3f2Gw2AUTNumyPDHeQMIPgmp04Igl9hbsF3x0nmW3anJKFOWtZeMz3AJc9OuVoipyD2TwmDXzFjlnLOTrRad/ghp0hc1AA0PsAkxh/mVXSnIKQcDtVTMm2SdZRPt1wn/HLyHAnQBDA5kSSSSbNbJ60qpx8OggI0Y5Z1x0rhXXH4JPVJWdcAYTmUd8ZOtAh3qSamHwCFBWBoxieSSuRaIP8l9UIJglM3OpVykR6zVHSrJ/dDWYRfNGOVJs8b5ggn/yemTFuEU2A+wiPAFbJZIKdw9kNa0gRjOcYjz03iCCWuKvFKAdcEN2iWJZNI4Qwg+ir2SwmCV2NjwnWTHbldcahx8Ehdh1M0YpMnwU61D3ascPYcAYUHZDjFKMYesVpx/yakGAuDUzprx3LPSuVecKoipkgKhQAzY8J3kxvhWiapIPwcD9IFNm3HIMdK410nqCDwSwHXAjI5xSjEGOReJfty9BgKhAM3bsApxE+5WnKsdvdCD4cENWPBcpZIuVwg+HX8QwvSVTVixiLBGrJYfKd2o0hZ2Vc0b5ImkxzmXHarcPUZXYJfNDmWIcRJ5VV8+nL3GwjXADJjwHLOT+UIfagrp04KgFcwbcIpwhSzCSesKvVMCNcFOzmWIcMYtg51pyr2HgyCAmM/lSjFGOVcdKpy8ksLhVRjY8V3kRjhXHatIPQYCtVfNTqXJcNPtFghpnH9HAmAUjA+kSXOT+EPJatx/EwBglY1aMBywBiyX3T4JKQYXtdTOmKRJ84c5Fkl/CD1G1vVAGA9kCeTSOVUfP0n80IL0AA7Opcox060XXD9dfAeAIRWOmKVJ8Qc5g4nryv9S13UAmNikHOSSLJYca4m90MNgFU3PpAklU21CX2ucfZDANlRMWrCcJQZ5AwlqiHwSV7TVjFukyLFTrVaIqYr8RlZ01E6YpcgzhjhXyerKvwfD4JQZG2XIccUuAgiqXKkSFzQBDtvmyiSFeVacv119hhc0143P8YgkUi1VCCpJ6YfWdFUM2KXJsUU5l4hqSGnGQiFVzQ5wCDHT+RZc/sg/Uxd0QcwY8dykkq3VHWvIvBJXdFeOjiaJsMdsAhw/Sf8TAuDADJilCLATrdeJ65x9k0B0Fc7bZsnlE+2XCetIvwcWddVMDmQKcMeswt1+iXySFuHAzpilCbESbZbJasr9U4Bg1NjYsJzlByxVHP8JKYeDIBWY2mbdcVI5AgnpifwTlvRXzZswSTGHeZUc65wp0lc1wUzb8YnkR7lDnKtdqFPANNVN2LCdZEdtgkhqyekG12DXzVvwnfCGeJec6oq/Rtc1FE1b5ciw0i1W3StdfBIAdUCYW6adJYctFki+iv2SQGHUWRiwSiTFeNedf8koU5aggU6asVwlk/jD3P/IaQYDdQEOziRdM4asFx9qXWjHgiFBzc4lHCRSrlcfa4l/U1c2FFjYsF0xxW4W3z7K/dJCoRXM2yac8QatFohpyT3QgDXBGQ4lCfAT7JZfKYr9UkN0VdkapAmkh21D3KscaMcCNdRYWLHI5JI5FR8+CfyHgjQAjRrx3WRH7RfJf8h9ENdhF41aJNwxBy2XCCmdqcYCdcHYGKRJsEY4VUnqXD0SQvTA2BixSCVTeNdcKkhpkMA0FMxPsUlzhXiWiH6JPVDCdZVZmyaJsUY5FUi/yvxGwvQXjNowXLCTbNVca0qoE8BhAM6YsUkk07kWnT9KqcbXYIFMG6Wd84d5VV3/3ekTQGHXjtulSXCTrJUdfpw/E9egAI2b5MgwE/iVXSnIacbCdIAMTqScJIc41h3/yHxH1vUUmNoliGWSeNacvgn8x8Ph15mPpdzkRTkXHL9IKEcWYRTY22Qc85OtQh1+3KgS17WVGA6knSVG+MIJ6oi800Jg1BnOMZzxxy5XSavcqYbXYMFO2/Fd5QZswwmrnbzHAnXBWBilSXFTrZaIqknox9egFRhOpBwkhTiWHSuIqZDAdRXYT6UIMEUuVUh/3KhSw3ZBWQ9xiDDSrAMdv0q9x8Ih1UwbpIklhm0WiKsIfRLDdRfYTqXJJJN5Vgh/SLwSQDZBDZvkyjAG7YPJq4mpkwO0lcxaZpykU7iVCX8I6BLCdQENT2SKJNOtFwh+Hb0GF7VVjc6linEFeNUdPwmpx5e0VcwP5tylR/jVSL8I6QfCthSZmyTIZIZsFl8p3XxT13XXjZsx3LETrRbfKZyoRlagAM6OsIpwxrhDnb8caEcD9hXYzjFc8VPuF4nqXXzQ1zZVDRilXXHTrgOdqh3o0MI1wU0aZQjxB/kX3X6IqQcXoRXMTrAIpYdtQ4grCDwTAjQA2NqkybBHeFccagjo0Ne1FMzOpcikx22XXSpKv0ZWYNVZznGJsQd5QghqSb2HlyFB2c6xiiTH7ddfKhy8BkK2F9kaZoglEjkC3SvcKNCCYAEYG+Td8QftF1w/SfyGAvVBDNuxiaVHrhbJa8m/BwJ0lc3OZIiwU+wW334daBPDYNQO2uTJ5JKsV11+nGkSQqCU2c9knWSGLVac6gjoEgPgAJjPsUjxUrlWCf/cfccWoIDZjmRKJJO4w4mrnf3GFvSVmBqwCjFSOUJdqYipk4K1FNhYpt3xhjmXnetJPxIXNBXMjqTcMUft15w+yL1Hw+AUzY5lSjATrdVdP939xwO1182aMIkzk25WXGpdvFNDNhTOm2VdMEbsFlzqiX1HFrSVDI5mnOUHbFbfPwh9hwKh1NmOZUozxzhD3X/JPdMDdICM2yadMUas1snpnXzQ17RVTtumibHSbBdd6919RtchVI6OJYglU23X3f4JKFOCtlWMTmUKMNKt19wqCD3GwzWBDFjk3TASeNdIfp19xsI2QVjb5Fywhy5Wib4KqAYCoIFMGPBKZQUuV8gqXahTwnXXzM5miSRTuIPIv0qpx5d0FJjbZAnxEm3CSeod6cbDYcCYTjCdMUd5lRz+iLzGQqFUTZqk3WWT+UPd/wr9xxZhAU0OcF3xUm5WnT7JKNPCIJQOmzHdcEb4VR0rSP1Qg/TUjM4myDHHuReJvwroR9Z2AVnacd3xR64CHWsdvBCXtlRYznAJMMZ41wl+iHzQ1vWAzpuk3DAHbQMfKsk/Etd1QA3OMF3wBi3DyD4cvAcAIIFNGqSJ8NPtg99qnekQ13ZVTo5wHXPHbVfdahy9kgN2V40bpRwzx+1WCf9I6QZC9ADMGzGd5ZKtVkgpyv9Hg7SXzZpx3WRGbcIfacn/RwB2QQxOcdwxh3jCSb8IqMfDIJVMG+QI8ZNuA9w+3X0TgvWVWBsxSHHFbdeIKdx9E0P1gA6b5Z3zkqzCX2nJqZKCNZVYTnFcpUV4lgipnf0Sl2DVWA9liOTT7YOcKgh80teglVjaJZ1lEniWSf8JPAfC9IANmibdcMUtgwiqSL3HFyHBTc4kSaSGrNddawi8kwN1ANhPZAlkk7mVXz6cfRODNMHNWyWI8Ebtgx9/yegQwuHBTE6lXXBG+UOc6t19EIK0AMxOJAmwxvlVHGocPBCAYMEMmubKMZPtF4mryXyGw6ABzdiwiSUGeFcJfwqoU1b2FA6apsjxxnmXCD9J/EZXIcEMTiXJsMUt1RyrXD2SF6DAGZowHLCFbFVd60mpk5egl40PZRzxRSyCXz7JfZIWdFQMGyTc5Id5g91pnfwH17TUmY4l3TGHrYPcacj8U0P1FQ7OpJywh+0CXf6I/VOCdFWOjiVJscUtAhy/yegHg3QU2M/knOUGLJcJfwrphkM1186OJYjxBS5X3StcKAZDocENz6WJJEYsF8i/yT9SFzTUmM/lHTFGrULcK0gpx4A2QVjb5N1xhu5D3GrI/QYXoQHZjiWJZEdt1klpiL2HlqCBTs5lyCWSOUMcq0hpBkOgAJnasIkwR7mWiH6IadCW4VVYGqbcM8Z5Q52rXL9SFqDUWc4liiRG7ZUdKggoUIPh1MzbZApwkjmXHf9IPUZXdhRZm+WKcQUsVsn/yPzHAzYVDFvwHSRGOZdd6x2/Btc11YzOJshwRu5X3yoKvxOD4cAMz2WKcZJsVt1/XX0TwzXVDM6kSfOT+EOIfhy9ksKh1dhOpUnxU6yXiGmIqBICdMDMjiac5Ubsl58r3anTFnXUWY5mnDCTuYMJal18E0P11NkbZN1wU+yWnesdvBOC9hWZGuQcpQet1whpyenHF2HUjQ4wiTFH7Bdfaog9Uxa0VRnaZYixUq1DHCucvZMDYVfNGKaIsIUsAlxpiOhSQ/SAjJslSLDSbZYca0jpENbhQM0OMAkkRvjVH34dvFMDdZXNDiQIsROsVl8+CHwHAjUXjQ9kXWSG+EMdPohoUhagF42OpBwwxi0XCetcPFKD4QEZDjAJJFKtAl3/XD9HAvXAmNvwHXBG+MMdqZw8RgJ01E6aJsizhTiDCf6JaYfCNVQMj/HJJMb4VglqCWnHl3RUDY/kiHFTuZYdad1pxgJ0FRhOcZ0wR+3CSCoJf1JAIQDMzibdMdKsggh+iChHgqEVjE6wHWRHrBUJf8k8Uxag1BkP5cjlRm4WnGqIvxOAIdSZ2nBdMdI5QhyqCCkGwvZAGQ+xXDAH7ZUJvt2/EoO0lUzbpolkhywXCGmK/xLC9dWND+UJ5Qf4w8iqSOgGF3UA2NtlXLDSrkJffx2/U5ahFE6OpUkzkmxCXGnJaNOCdlSNmvAIJZNtlR0piTzQwHVUDI/lHDPFLBdcqgm/RkLhVYwPpYnxBTmXHf4dfBNXdYAZj3FdJYatA50q3fzG1zSVTtjwHCTT7VUdK0roRkA0wM3P5RzlE64XyD4cvRJDYNTZ2uWJsROtwl1qiLxQ1rQUDA+wiORGbQIJ6gjo0pc0QA6PpAmlhiyDHevI/0bXNIFYD+bKMAbtll8+iv3GVnQVzFjmiTEHbBfcP1w80IJglY1OcIhwU+3DnamJvJLCIIDMGKVIsMZ414i+CD9QluCA2c/mnDDGeRVcqly9kIP2FcwacApzhvhCyaudfcbCdhRNz2SIMVPsV4np3WgH1rYAjNumyPDTeRcdfpwoxxehVMwP5pwkknhCHGsJP0ZWYUEZzqXcsIYsQlx/SXySgnYVTU+lyaTSbVbdvpx8RsMhVFnPpEjzki5DHCoJf0eAIdeYT/BI5Ecs1V0+yT8HF7VVzQ6xyiWSOUPJf0l9UgJ0VdgapYpz0/mDCKsIPwfCdIDND3CJMdJtAx2qXbxGF6AUzQ/wCaTTuMOdagkoUpd2F40a5p0z0mwDyauJPJICtMANj6TI8QVtQkhqnX9GAHVBWc4l3TGTrdUdv1x/ElZ2Fc3P8AolBWzXiKpcvRMDtYEZGLFd8UYuV93piXyS16EUGY5kiOWHrIJJagn/R4PhAUwa5AkwxW4C3GscaNMWdFTZD/AcJUcuVVx/yf0H1vYVjRuliSWSrFccqpy8UIP1gJnaJZzxxS4Xnz9cPIfDNVXOzqTdMccuFwhrCHyTVrYUzM/x3OWSuIMJ6pxoUxZhAJhb8J3wxy1CXWvcPwZDdgHMjmRJsUZsVkm/CCnGV2DUTpvwHDPTrFadK9x8k4M0Ac1P8V3lhrjVXKsIP1MW9ICMWLCJJRKtVV2rSChSgvQBDQ5lyKSHORVcqogpB8PhVYxPcAjlhu3WXX8cfFJXNYCOj6Td8RK4wh0rXX0Tg/SUmM5kySSH7cPfaom9U8B0gI6P8d3xx24DnemcqRLXdICYWLCc89JuFh0piX1HlzUBDE+kiCSSOILIf9y905ZgwQyP5QmxByyXHStJ/QbCINRO2qbc8Mb5AlxpiHzQwGEBTVuknDEGbJYcKwmoEkI115jPpIkkkjmCH34JfNKXdJWNmvBc5Ib5l4m/Hb9GwvRX2RpmiKWHLFVcqkr8BsA0AcwOJAkw0qyX3CrIqYYXtFQOz6RJ5ZKtVpzryX8SQ7YVTBixyPCH+ILJ6gi/UNb019nPpAgz0/jW3aqcKdKDtQDZmyacpRJsVQg/yOgQluFV2c+libFSrFdc6cg9xtZhwdhP8IjwBqwW3L8J/VNCdMEZ2yaJs5Kswx8rSemGQnRVmFoxnfESrkOdqoi8EgI01IzPpZ3lEmzD3KtdfFOAYBSYGPGd84c4VVz/CKnGQHSA2NuwSKSSbdUd6sg8EsK2AdnapN0w0mwC3CnI/BLDtJVOz6VJMQf414gpyCkSw3ZAmFumnLHHrkIIqskoUhd1FI1apEpxU24WCGvcfxMCtlWZGLAc8IdsAt9+iLxSArQAmc+mySSSLgPIq4i8RlZ1VE2aptzzhSzDnOtJPBDANQDYDmXcsYZsFp3+yegTVmEUjtqkCnDTuYPcvxy9EldhF9kPptylB+yCX36dvxJXIJVN2/HdMEbsF98+CH3GFuFAjFrlnPAGbMIc6ojp0oP01NmOMIowRjmX3D4d/dCXIAEZD6aJ88UtlV8qiT2GwHQX2Q6lnTDFLYJd6wnpB5bglFkYpApkxyyWH38K/ZPWtRUM26RdZEe4g5xq3DyTgHVUzY+mnXBSrgPIKd2pE0MgARgOsAkkk20D32pIfNOAYUDMGmRJMIc41h9qCf0HgjYVTRqlSGUFbdddf9y9BkK11djPsUhkx3hCyaoIvJCDIBeM26bdcYfuA5z/yOjSFmABzM+lCLPTrQJcvpyoRsAhwNkYpEmwB63XyKuJPVIWdVTYz2TI8YfsVknpyH3GQyDAzdskXTESuIIdftwoxsMgwRkPZEgxBiwXXKvdvFOWoADNmnGJMdIsF8hqSGgHAHUUjY9kSnDTbZaIagkpkhcgFdnbZcpxUmwXXKrIKQfW9NSYT2VIcYasAl8qiSjQwqHBGM9liGUTrNffakh80hagF5nPZEoxh3lCCGpKqYeAYUCM2iSI5MVsV4hrHX0QlrUAGdjwnLPSbRVcv0q8khch1Y6bcYlxhS3VXyqdaFJWoAFMz2RKMRN4Vh1qyL2TAzVVDc9xSaVSLFbdKgr8h4KhVdmYpQmlh2wWXz6daNMCtFeYGOSIcUZ4g52/Xb8TgGCXmQ9kHeWH7BdIfwip0Jd1F9jbpYglUrkWHCqIPQeD9VSZjqRJZMUsQsipiPxSQvWBDBixSWST+ULcfwk8x9ZggVmPZcizxSwXHGmIfVLCtZQN23CIJIYsFUh+3b1TFqFAjY/wnPOHOZUIKYk90wM0VY7PZIpkhviVSH6d6RDANNSMz2XdMMZtVxy/SDxGFzXUDJrkCKWG+UPdvwi80Je0AJhbpQhlkm5DyD9JPYfXYAENGmQdMNJtVh1/HGjQgGHUGE9lnWVGOMPIaclo08J1181PpJwwRW3VCemcPBMC9UAZm2bc5YU4lt8+Hb1SFmFAzZiliLASrJfd6wh9RsP0gBnPZopkxS1CSH4daRDXoRRNG2WIcQdtQwlr3GmGV6FAmFokiHETrELJvsj9UkI0QAzOpAklknkXHOuIfZNCNdVMT2adMNNtgx9ryfySVyDUjQ/kyTDFbRfJ65w90wL0lBjOJYpwkmzVHOvcPdPC9NVZ2PCJcAUtF1z+CX2SVyFXzY9wiSTH+JVcKdw8h8LggM0Oscgw0rmCXGmIKBKCNNSO27BcMdOsFx1rnH0TAjSBTtoxyjHGeRdJ/sjo04I11A3PcInlRvjWyX8cvwfAINeNjrGIc5Otl12rSLzTg+CBWQ9kHKVTuRcIKp290ld2VE0Y5Fzxx+xD3z/IaFKDIcHZj3BI8NNtlp2p3emGwvTA2BixiPOSuYJdfgnoxlehQMyaZMhkh3hDyL7I/RKXNVTOmvGJMMasVgl+CTzSFrQX2A9xiPPGrIMc6YjpkhbglQ6bZojx025C3D8IPFLXtdRMDrHKcIbt1gi/XajQwHYV2ZrwnCVTeQJd68rpE8LggUwPcZwlRnkWHaqIfEbXtUDMm2SJ8cVslhypyCgGArZVzZuwiLBSrRUdPh1o08BhFExY5oolUrmWXyrIfRIDYRSNz3FJMJK5V90qCPxQgHUAGZrkSPESuFbJ/ty804K1FFjPcJzwxW4DCavdqRDDIRSNG+TJpIVt1kir3X1GV3XUmY="
      );
      HashSet var1 = new HashSet();

      for (int var2 = 0; var2 + 64 <= var0.length(); var2 += 64) {
         var1.add(var0.substring(var2, var2 + 64));
      }

      return var1;
   }

   private static long mix(String var0) {
      long var1 = 1125899906842597L;

      for (int var3 = 0; var3 < var0.length(); var3++) {
         var1 = 31L * var1 + var0.charAt(var3);
      }

      return var1 == 0L ? 1L : var1;
   }

   public static boolean live() {
      return true;
   }

   public static boolean sync() {
      return true;
   }

   public static boolean owner() {
      return true;
   }

   public static String uuid() {
      try {
         return MinecraftClient.getInstance().getSession().getUuidOrNull().toString().replace("-", "").toLowerCase();
      } catch (Throwable var1) {
         return "";
      }
   }

   public static String name() {
      try {
         return MinecraftClient.getInstance().getSession().getUsername();
      } catch (Throwable var1) {
         return "?";
      }
   }

   public static String normalize(String var0) {
      String var1 = var0 == null ? "" : var0.toUpperCase().replaceAll("[^A-Z0-9]", "");
      if (var1.startsWith("LCORD")) {
         var1 = var1.substring(5);
      }

      return var1.length() != 12 ? var1 : "LCORD-" + var1.substring(0, 4) + "-" + var1.substring(4, 8) + "-" + var1.substring(8, 12);
   }

   public static boolean isKey(String var0) {
      try {
         byte[] var1 = MessageDigest.getInstance("SHA-256").digest((d("F9pjnk/zLivsd7k=") + normalize(var0)).getBytes(StandardCharsets.UTF_8));
         StringBuilder var2 = new StringBuilder();

         for (byte var6 : var1) {
            var2.append(String.format("%02x", var6));
         }

         return RD.contains(var2.toString());
      } catch (Exception var7) {
         return false;
      }
   }

   public static boolean eval(String var0, String var1) {
      if (var0.isEmpty()) {
         return false;
      }

      if (OW.contains(var0)) {
         sig = mix(var0);
         return true;
      }

      if (var1 == null) {
         return false;
      }

      if (var1.startsWith("R1:")) {
         String[] var7 = var1.split(":");
         if (var7.length == 3 && var7[2].equals(var0) && isKey(var7[1])) {
            sig = mix(var1);
            return true;
         } else {
            return false;
         }
      } else {
         String var2 = var1.replaceAll("[^A-Za-z0-9_-]", "");
         if (var2.regionMatches(true, 0, "LC-", 0, 3)) {
            var2 = var2.substring(3);
         }

         try {
            byte[] var3 = Base64.getUrlDecoder().decode(var2);
            PublicKey var4 = KeyFactory.getInstance("Ed25519").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(PK)));
            Signature var5 = Signature.getInstance("Ed25519");
            var5.initVerify(var4);
            var5.update((d("F9pjnk/zLivsd/8=") + var0).getBytes(StandardCharsets.UTF_8));
            if (var5.verify(var3)) {
               sig = mix(var1);
               return true;
            } else {
               return false;
            }
         } catch (Throwable var6) {
            return false;
         }
      }
   }
}
