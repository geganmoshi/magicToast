public class MagicToast {
    private int residenceTime = 0;
    private View view;
    private static WeakReference<View> oldView;
    private String message;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    animateViewOut();
                    break;
            }
        }
    };

    public static MagicToast make(View view, String message, int time,int status) {
        MagicToast magicToast = new MagicToast();
        magicToast.setDuration(time);
        magicToast.setMessage(message);
        magicToast.setView(view,status);
        return magicToast;
    }

    public void show() {
        animateViewIn();
    }

    public void setView(View view,int status) {
        if (oldView != null && oldView.get() != null) {
            View old = oldView.get();
            if (old.getParent() != null) {
                handler.removeMessages(1);
                ((ViewGroup) old.getParent()).removeView(old);
            }
        }
        ViewGroup viewGroup = findSuitableParent(view);
        if (viewGroup != null) {
            this.view = LayoutInflater.from(view.getContext()).inflate(R.layout.item_magic_toast, null);
            viewGroup.addView(this.view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            TextView textView = this.view.findViewById(R.id.status_text);
            textView.setTypeface( Typeface.createFromAsset(this.view.getContext().getAssets(), "fonts/Muli-Regular.ttf"));
            if (textView != null) {
                textView.setText(message);
            }
            ImageView imageView =  this.view.findViewById(R.id.status_img);
            switch (status){
                case 0:
                    //默认
                    imageView.setVisibility(View.GONE);
                    break;
                case 1:
                    //成功
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setBackgroundResource(R.drawable.icon_magic_toast_success);
                    break;
                case 2:
                    //失败
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setBackgroundResource(R.drawable.icon_magic_toast_fail);
                    break;
            }
            oldView = new WeakReference<View>(this.view);
        }
    }

    private void setMessage(String message) {
        this.message = message;
    }

    public void setDuration(int time) {
        if (time > 0) {
            residenceTime = time * 1000;
        }else {
            residenceTime = 2000;
        }
    }

    private static ViewGroup findSuitableParent(View view) {
        ViewGroup fallback = null;
        do {
            if (view instanceof FrameLayout) {
                if (view.getId() == android.R.id.content) {
                    return (ViewGroup) view;
                } else {
                    fallback = (ViewGroup) view;
                }
            }
            if (view != null) {
                final ViewParent parent = view.getParent();
                view = parent instanceof View ? (View) parent : null;
            }
        } while (view != null);
        return fallback;
    }

    private void animateViewIn() {
        if (view == null) {
            handler.removeMessages(1);
            handler = null;
            return;
        }
        ObjectAnimator anim = getAnimationInFromTopToDown();
        anim.setInterpolator(new FastOutSlowInInterpolator());
        anim.setDuration(250);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                handler.sendEmptyMessageDelayed(1, residenceTime - 500);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }

    private void animateViewOut() {
        if (view == null) {
            handler.removeMessages(1);
            handler = null;
            return;
        }
        ObjectAnimator anim = getAnimationOutFromTopToDown();

        anim.setInterpolator(new FastOutSlowInInterpolator());
        anim.setDuration(250);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (view != null && view.getParent() != null) {
                    ((ViewGroup) view.getParent()).removeView(view);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }

    private ObjectAnimator getAnimationInFromTopToDown() {
        return ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
    }

    private ObjectAnimator getAnimationOutFromTopToDown() {
        return ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
    }
}
